package com.pathdx.utils;

import com.pathdx.exception.LabNotFoundException;
import com.pathdx.exception.UserNotInLabException;
import com.pathdx.model.LabDetail;
import com.pathdx.model.Role;
import com.pathdx.model.UserModel;
import com.pathdx.repository.UsersRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidationsUtils {

    @Autowired
    UsersRepository usersRepository;

    public static  void labValidation(String labId, UserModel userModel) throws LabNotFoundException {
        if(userModel.getLabDetails().isEmpty())throw new LabNotFoundException();
        List<String> labs = userModel.getLabDetails()
                .stream()
                .map(LabDetail::getLabid)
                .toList();

        if(!labs.contains(labId))
           throw new LabNotFoundException();
    }

}
