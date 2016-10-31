package com.vvasilyev.tutu;

import com.vvasilyev.tutu.service.SearchService;
import com.vvasilyev.tutu.service.ServiceProvider;
import com.vvasilyev.tutu.model.SimpleStation;

import org.mockito.Mockito;

/**
 *  Base class for Tests
 */

public class AcitivityTest {


    protected void mockSearchService(SimpleStation[] array, Object methodCall, SearchService mockService) {
        SearchService.StationMatrixCursor cursor = new SearchService.StationMatrixCursor();
        for (SimpleStation station: array) {
            cursor.add(station);
        }

        Mockito.when(methodCall).thenReturn(new SearchService.StationCursor(cursor));
        ServiceProvider.instance().use(mockService);
    }
}
