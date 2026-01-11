package com.cloud.test.device.service;

import com.cloud.test.device.domain.Devices;

import java.util.List;

public interface IDeviceService {
    List<Devices> getIosDeviceList() throws Exception;

    List<Devices> getActiveDeviceList();

    String getRealDeviceUdidList() throws Exception;

    String executeCommandForBytes() throws Exception;

    boolean startSimulator(String udid);

    boolean startApp(String buildId);
}
