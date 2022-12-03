package situation.service.impl;

import com.situation.entity.Delay;
import com.situation.entity.DelayMap;
import com.situation.entity.PredictMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import situation.dao.Mapper.DelayMapper;
import situation.dao.Mapper.PredictMapper;
import situation.service.DelayService;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lgd
 * @date 2022/11/8 14:02
 */
@Service
public class DelayServiceImpl implements DelayService {

    public static int count=0;

    @Autowired
    private DelayMapper delayMapper;

    @Autowired
    private PredictMapper predictMappper;

    @Override
    public List<Delay> selectNewData(Integer id) {
        Example example=new Example(Delay.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThan("id",id);

        List<Delay> delays = delayMapper.selectByExample(example);
        return delays;
    }

    @Override
    public PredictMap selectNewPredict() {
        return predictMappper.getPredict();
    }



    @Override
    public List<Delay> selectAll(){
        return delayMapper.selectAll();
    }

    @Override
    public DelayMap selectNewDelayMap() throws ParseException {
        return delayMapper.getNew();
    }

    @Override
    public List selectNewAll() {
        long start = System.currentTimeMillis();
        List<Object> res=new ArrayList<>();
        new Thread(()->{
            res.add(0,delayMapper.getNew());
        }).start();
        new Thread(()->{
            res.add(predictMappper.getPredict());
        }).start();
        //限制2s
        while (res.size()!=2 && (System.currentTimeMillis()-start)<=2000){

        }
        return res;
    }


    @Override
    public Delay selectId(Integer id) {
        Delay delay=new Delay();
        delay.setId(id);
        return   delayMapper.selectByPrimaryKey(id);
    }


}
