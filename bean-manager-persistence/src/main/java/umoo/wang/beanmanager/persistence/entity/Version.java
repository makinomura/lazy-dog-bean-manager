package umoo.wang.beanmanager.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import umoo.wang.beanmanager.persistence.support.PrimaryKey;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by yuanchen on 2019/01/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "version_t")
public class Version implements PrimaryKey<Integer> {
	@Id
	private Integer id;
	private Integer appId;
	private Integer environmentId;
	private Integer num;
	private String versionName;
	private Date publishTime;

	@Override
	public Integer getPrimaryKey() {
		return id;
	}
}
