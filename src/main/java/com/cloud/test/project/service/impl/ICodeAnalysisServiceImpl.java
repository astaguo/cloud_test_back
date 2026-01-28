package com.cloud.test.project.service.impl;

import com.cloud.test.project.domain.CallGraph;
import com.cloud.test.project.service.ICodeAnalysisService;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Service
public class ICodeAnalysisServiceImpl implements ICodeAnalysisService {

    @Override
    public CallGraph analyzeProject(File projectRoot) throws IOException {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        // Add all source directories
        // Assuming standard Maven/Gradle structure, we look for src/main/java
        File srcMainJava = new File(projectRoot, "src/main/java");
        if (srcMainJava.exists()) {
            combinedTypeSolver.add(new JavaParserTypeSolver(srcMainJava));
        } else {
             // Fallback: add project root if src/main/java doesn't exist
            combinedTypeSolver.add(new JavaParserTypeSolver(projectRoot));
        }

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(symbolSolver);
        JavaParser javaParser = new JavaParser(parserConfiguration);

        CallGraph callGraph = new CallGraph();
        
        Collection<File> javaFiles = FileUtils.listFiles(projectRoot, new String[]{"java"}, true);

        for (File file : javaFiles) {
            try {
                Optional<CompilationUnit> cuOpt = javaParser.parse(file).getResult();
                if (cuOpt.isPresent()) {
                    CompilationUnit cu = cuOpt.get();
                    String filePath = file.getAbsolutePath();
                    
                    cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
                        String className = c.getFullyQualifiedName().orElse(c.getNameAsString());
                        callGraph.getClassToFileMap().put(className, filePath);
                        callGraph.getMethodToClassMap().put(className, className); // Self map for class-level changes

                        // Check for Controller annotations
                        if (c.isAnnotationPresent("Controller") || c.isAnnotationPresent("RestController")) {
                            callGraph.getControllerClasses().add(className);
                        }
                    });

                    cu.accept(new MethodCallVisitor(callGraph, filePath), null);
                }
            } catch (Exception e) {
                System.err.println("Failed to parse file: " + file.getPath() + " - " + e.getMessage());
            }
        }

        return callGraph;
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<Void> {
        private final CallGraph callGraph;
        private String currentClassName;
        private String currentMethodName;

        public MethodCallVisitor(CallGraph callGraph, String filePath) {
            this.callGraph = callGraph;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            String prevClass = currentClassName;
            currentClassName = n.getFullyQualifiedName().orElse(n.getNameAsString());
            super.visit(n, arg);
            currentClassName = prevClass;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            String prevMethod = currentMethodName;
            // Simplified method signature: ClassName.methodName
            // Ideally should include parameters to handle overloading
            currentMethodName = currentClassName + "." + n.getNameAsString();
            
            callGraph.getMethodToClassMap().put(currentMethodName, currentClassName);
            
            super.visit(n, arg);
            currentMethodName = prevMethod;
        }

        @Override
        public void visit(MethodCallExpr n, Void arg) {
            if (currentMethodName != null) {
                try {
                    ResolvedMethodDeclaration resolved = n.resolve();
                    String calleeClass = resolved.declaringType().getQualifiedName();
                    String calleeMethod = resolved.getName();
                    String calleeSig = calleeClass + "." + calleeMethod;
                    
                    callGraph.addCall(currentMethodName, calleeSig);
                } catch (Exception e) {
                    // Resolution failed, might be library method or unresolvable
                }
            }
            super.visit(n, arg);
        }
    }
}
