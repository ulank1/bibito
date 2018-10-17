package com.ulan.az.usluga;

import com.ulan.az.usluga.service.Service;

import java.util.ArrayList;

/**
 * Created by User on 01.10.2018.
 */

public interface Searchlistener {
    void onSearch(ArrayList<Service> services);

}
