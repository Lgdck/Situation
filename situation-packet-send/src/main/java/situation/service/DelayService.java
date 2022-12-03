package situation.service;

import com.situation.entity.Delay;
import com.situation.entity.DelayMap;
import com.situation.entity.PredictMap;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author lgd
 * @date 2022/11/8 14:01
 */
public interface DelayService {


    public List<Delay> selectNewData(Integer id);

    public PredictMap selectNewPredict();


    public List<Delay> selectAll();


    public DelayMap selectNewDelayMap() throws ParseException;


    public List selectNewAll();
    public Delay selectId(Integer id);

}
