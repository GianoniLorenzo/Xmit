package com.bytezone.xmit.api;

import java.util.List;
import java.util.Optional;

public interface XmitPartitionedDataset {

    String getName();

    List<XmitMember> getMembers();

    default Optional<XmitMember> getMemberByName(String name){
        return getMembers().stream().filter(m -> m.getName().equals(name)).findFirst();
    }

    default XmitMember getMemberByIndex(int index){
        return getMembers().get(index);
    }
}
