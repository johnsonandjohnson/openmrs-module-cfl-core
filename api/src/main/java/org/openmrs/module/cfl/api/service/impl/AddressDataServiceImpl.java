package org.openmrs.module.cfl.api.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.addresshierarchy.AddressHierarchyEntry;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.cfl.api.model.AddressDataContent;
import org.openmrs.module.cfl.api.service.AddressDataService;

import java.util.ArrayList;
import java.util.List;

public class AddressDataServiceImpl implements AddressDataService {

    @Override
    public List<AddressDataContent> getAddressDataResults() {
        AddressHierarchyService addressService = Context.getService(AddressHierarchyService.class);
        List<AddressDataContent> resultList = new ArrayList<>();
        List<AddressHierarchyEntry> rootEntries = addressService.getAddressHierarchyEntriesAtTopLevel();
        for (AddressHierarchyEntry entry : rootEntries) {
            processData(addressService, entry, new ArrayList<>(), resultList);
        }

        return resultList;
    }

    private void processData(AddressHierarchyService addressService, AddressHierarchyEntry entry,
                             List<String> stack, List<AddressDataContent> resultList) {
        List<AddressHierarchyEntry> entryChildren = addressService.getChildAddressHierarchyEntries(entry);
        if (CollectionUtils.isEmpty(entryChildren)) {
            List<String> singleElem = new ArrayList<>(stack);
            singleElem.add(entry.getName());
            List<String> singleElemCopy = new ArrayList<>(singleElem);
            resultList.add(new AddressDataContent(singleElemCopy));
            singleElem.clear();
        } else {
            stack.add(entry.getName());
            for (AddressHierarchyEntry child : entryChildren) {
                processData(addressService, child, stack, resultList);
            }
            removeLastElementFromStack(stack);
        }
    }

    private void removeLastElementFromStack(List<String> stack) {
        stack.remove(stack.size() - 1);
    }
}
