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
}