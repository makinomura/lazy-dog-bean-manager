package umoo.wang.beanmanager.message.client.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by yuanchen on 2019/01/11.
 */
@Data
@AllArgsConstructor
public class FieldUpdateMessage implements Serializable {
	private String fieldName;
	private String newValue;
}
