package umoo.wang.beanmanager.persistence.entity;

import lombok.Data;
import umoo.wang.beanmanager.persistence.support.PrimaryKey;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Data
@Table(name = "application_t")
public class App implements PrimaryKey<Integer> {
	@Id
	private Integer id;
	private String appName;

	@Override
	public Integer getPrimaryKey() {
		return id;
	}
}
