package saleh.ma.mostafa.gmail.com.advancedplacepicker.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mostafa on 08/14/2017.
 */

public class AddressResponse {

    List<Address> results = new ArrayList<>();

    public class Address {
        public String formatted_address;
    }

}
