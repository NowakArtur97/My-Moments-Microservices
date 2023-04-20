package com.nowakArtur97.myMoments.userService.feature.resource;

import org.bson.types.Binary;
import org.modelmapper.AbstractConverter;

public class BinaryConverter extends AbstractConverter<Binary, byte[]> {

    @Override
    protected byte[] convert(Binary binary) {
        return binary.getData();
    }
}
