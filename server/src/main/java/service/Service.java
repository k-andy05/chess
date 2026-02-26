package service;

import response.ClearResponse;
import dataaccess.*;
import response.*;

// This class will take RegisterRequest and call correct sequence of service class methods and return a Register Result
public class Service {

    public ClearResponse clear () { // Will need to figure out how to check that db was cleared, then create ClearResponse instance and send it back
        DataAccess.clearAll();
        return new ClearResponse();
    }
}
