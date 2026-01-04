package com.cloud.test.service;

import com.cloud.test.ai.service.IDocumentService;
import com.cloud.test.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class IDocumentServiceImplTest {

    @Autowired
    public IDocumentService documentService;

    @Test
    public void testDelete() {
        documentService.deleteDocumentTxt(1);
    }

    @Test
    public void demo() {
        List<User> ids = new ArrayList<User>();

        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);
        User user3 = new User();
        user3.setId(2);

        ids.add(user1);
        ids.add(user2);
        ids.add(user3);

        System.out.println("================================================");
        System.out.println(ids.stream().map(User::getId).collect(Collectors.toList()));
    }
}
