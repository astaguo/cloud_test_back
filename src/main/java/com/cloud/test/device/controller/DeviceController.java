package com.cloud.test.device.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.device.domain.Devices;
import com.cloud.test.device.service.IDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理接口：返回 Shell 命令执行结果给前端
 */
@Tag(name = "获取当前设备列表",description = "获取当前设备列表操作接口")
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private IDeviceService deviceService;

    /**
     * 获取 iOS 模拟器/设备列表
     * @return 设备列表（文本格式）
     */
    @Operation(summary = "获取iOS模拟器设备列表",description = "获取iOS模拟器设备列表")
    @GetMapping("/ios/list")
    public AjaxResult<List<Devices>> getIosDeviceList() {
        try {
            return AjaxResult.<List<Devices>>me().setResultObj(deviceService.getIosDeviceList());
        } catch (Exception e) {
            return AjaxResult.<List<Devices>>me().setSuccess(false).setMessage(e.getMessage());
        }
    }

    /**
     * 获取真机 UDID 列表
     * @return UDID 列表
     */
    @Operation(summary = "获取iOS真机设备列表",description = "获取iOS真机设备列表")
    @GetMapping("/real/udid")
    public AjaxResult<String> getRealDeviceUdidList() {
        try {
            return AjaxResult.<String>me().setResultObj(deviceService.getRealDeviceUdidList());
        } catch (Exception e) {
            return AjaxResult.<String>me().setSuccess(false).setMessage(e.getMessage());
        }
    }

    @Operation(summary = "获取激活的模拟器",description = "获取激活的模拟器")
    @GetMapping(value = "/active/list")
    public AjaxResult<List<Devices>> getActiveDeviceList() {
        return AjaxResult.<List<Devices>>me().setResultObj(deviceService.getActiveDeviceList());
    }

    @Operation(summary = "启动模拟器",description = "启动模拟器")
    @PostMapping(value = "/start/{udid}")
    public AjaxResult<Boolean> startSimulator(@PathVariable String udid) {
        return AjaxResult.<Boolean>me().setSuccess(deviceService.startSimulator(udid));
    }

    @Operation(summary = "启动App",description = "启动App")
    @PostMapping(value = "/start/app/{buildId}")
    public AjaxResult<Boolean> startApp(@PathVariable String buildId) {
        return AjaxResult.<Boolean>me().setSuccess(deviceService.startApp(buildId));
    }

    /**
     * 截图
     * @return 截图流
     */
    @Operation(summary = "获取模拟器截图",description = "获取模拟器截图")
    @GetMapping(value = "/screenshot")
    public AjaxResult<String> getSimScreenshot() {
        try {
            // 调用工具类执行截图命令，获取字节流
            return AjaxResult.<String>me().setResultObj(deviceService.executeCommandForBytes());
        } catch (Exception e) {
            // 异常时可返回空字节数组或自定义错误图片字节
            throw new RuntimeException("截图获取失败：" + e.getMessage());
        }
    }
}