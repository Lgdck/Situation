package situation.dao.Mapper;

import com.situation.entity.Delay;
import com.situation.entity.DelayMap;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author lgd
 * @date 2022/11/8 13:57
 */
public interface DelayMapper extends Mapper<Delay> {
    List<DelayMap> getDelay(String sendTime);

    DelayMap getNew();
}
