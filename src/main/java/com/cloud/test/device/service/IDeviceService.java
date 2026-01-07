package com.cloud.test.device.service;

import com.cloud.test.device.domain.Devices;

import java.util.List;

public interface IDeviceService {
    List<Devices> getIosDeviceList() throws Exception;

    String getRealDeviceUdidList() throws Exception;
}
