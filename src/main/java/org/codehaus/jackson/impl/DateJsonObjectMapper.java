package org.codehaus.jackson.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.doukids.web.utils.JacksonUtil;


@Component
public class DateJsonObjectMapper extends ObjectMapper {
	
	public DateJsonObjectMapper() {
		CustomSerializerFactory factory = new CustomSerializerFactory();
		factory.addGenericMapping(Date.class, new JsonSerializer<Date>() {
			@Override
			public void serialize(Date value, JsonGenerator jsonGenerator,
					SerializerProvider provider) throws IOException,
					JsonProcessingException {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				jsonGenerator.writeString(sdf.format(value));
			}
		});
		this.setSerializerFactory(factory);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T readValue(InputStream src, JavaType valueType)
			throws IOException, JsonParseException, JsonMappingException {
		return (T) _readMapAndClose(_jsonFactory.createJsonParser(src),
				valueType);
	}

	protected Object _readMapAndClose(JsonParser jp, JavaType valueType)
			throws IOException, JsonParseException, JsonMappingException {
		try {
			Object result;

			org.codehaus.jackson.impl.Utf8StreamParser p = (org.codehaus.jackson.impl.Utf8StreamParser) jp;

			 
			byte[] by = p._inputBuffer;
		 
			String jsonStr = new String(by);

			JsonToken t = _initForReading(jp);
			if (t == JsonToken.VALUE_NULL) {
				result = _findRootDeserializer(this._deserializationConfig,
						valueType).getNullValue();
			} else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
				result = null;
			} else {
				DeserializationConfig cfg = copyDeserializationConfig();
				DeserializationContext ctxt = _createDeserializationContext(jp,
						cfg);
				JsonDeserializer<Object> deser = _findRootDeserializer(cfg,
						valueType);
					result = deser.deserialize(jp, ctxt);
			}
			
			Map resultMap = JacksonUtil.readValue(jsonStr, Map.class);
			String jsonMes = JacksonUtil.toJSon(resultMap.get("message"));
			jp.clearCurrentToken();
			return result;
		} finally {
			try {
				jp.close();
			} catch (IOException ioe) {
			}
		}
	}

}
