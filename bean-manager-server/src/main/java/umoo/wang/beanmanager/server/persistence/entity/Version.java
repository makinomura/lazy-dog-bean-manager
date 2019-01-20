package umoo.wang.beanmanager.server.persistence.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by yuanchen on 2019/01/20.
 */
@Data
public class Version {
	private Integer id;
	private Integer appId;
	private Integer environmentId;
	private Integer num;
	private String versionName;
	private Date publishTime;
}
