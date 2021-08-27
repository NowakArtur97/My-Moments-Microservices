package com.nowakArtur97.myMoments.postService.feature.resource;

import org.bson.types.Binary;
import org.modelmapper.AbstractConverter;

import java.util.List;
import java.util.stream.Collectors;

public class BinaryListConverter extends AbstractConverter<List<Binary>, List<byte[]>> {

    @Override
    protected List<byte[]> convert(List<Binary> binaries) {

        return binaries.stream().map(Binary::getData).collect(Collectors.toList());
    }
}
