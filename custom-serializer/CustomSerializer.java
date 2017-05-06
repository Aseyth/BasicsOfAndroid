import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Aseith on 06/05/2017.
 */

public class CustomSerializer implements JsonSerializer<ContainerModel> {

    @Override
    public JsonElement serialize(final ContainerModel containerModel, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        if (containerModel.getFirstName() != null) {
            jsonObject.addProperty("firstName", containerModel.getFirstName());
        }
        if (containerModel.getLastName() != null) {
            jsonObject.addProperty("lastName", containerModel.getLastName());
        }
        if (containerModel.getAge() != null) {
            jsonObject.addProperty("age", containerModel.getAge());
        }

        if (containerModel.getChildObject() != null) {
            final JsonElement jsonChildObject = context.serialize(containerModel.getChildObject());
            jsonObject.add("childObject", jsonChildObject);
        }

        return jsonObject;
    }
}
