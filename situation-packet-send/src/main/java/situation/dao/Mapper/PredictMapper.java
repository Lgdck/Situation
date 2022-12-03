package situation.dao.Mapper;

import com.situation.entity.PredictMap;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author lgd
 * @date 2022/11/29 15:36
 */
public interface PredictMapper extends Mapper<PredictMap> {
    PredictMap getPredict();
}
