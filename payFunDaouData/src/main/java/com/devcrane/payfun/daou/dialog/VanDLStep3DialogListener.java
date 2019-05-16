package com.devcrane.payfun.daou.dialog;

import com.devcrane.payfun.daou.entity.CompanyEntity;

/**
 * Created by admin on 7/19/17.
 */

public interface VanDLStep3DialogListener {
    public void VanDLStep3DialogEvent(String companyNo, String machineCode, CompanyEntity companyEntity);
    public void VanDLStep3DialogEvent(boolean isValid);
}
