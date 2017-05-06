import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * Created by Aseith on 06/05/2017.
 */

public class ChildObjectSerializer implements JsonSerializer<ChildObject> {

    @Override
    public JsonElement serialize(ChildObject childObject, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("address", childObject.getAddress());
        jsonObject.addProperty("phoneNumber", childObject.getPhoneNumber());

        return jsonObject;
    }
}