import java.util.*;

/**
 * @ClassName CmeModuleService
 * @Description TODO
 * @Author wzc
 * @Date 2020/6/8 11:48
 * @Version 1.0
 **/
@Service
public class CmeModuleService {

    @Autowired
    private AppLoginFeign appLoginFeign;
    @Autowired
    private ReadMapper readMapper;
    @Autowired
    private AppInfoUtil appInfoUtil;
    @Autowired
    private CmeIndexService cmeIndexService;

    public List getModuleCmeIndex(Integer versionCodeInt){
        Map<String,Object> map1= new HashMap<>();
        List<ModuleCmeIndexModel> listData = readMapper.getModuleCmeIndex(map1);
        List list = new ArrayList();
        for (ModuleCmeIndexModel listDatum : listData) {
            if (versionCodeInt>= listDatum.getVersion_code()) {
                if("AllMembers".equals(listDatum.getStyle_name())){
                    AllMembersModel allMembersModel = cmeIndexService.createAllMembers();
                    list.add(allMembersModel);
                }else if("ShanDongExam".equals(listDatum.getStyle_name())) {
                    ShanDongExam shanDongExam = cmeIndexService.createShanDongExam();//山东公共课考试入口
                    list.add(shanDongExam);
                }else {
                    Map<String, Object> data1 = new LinkedHashMap<String, Object>();
                    data1.put("title", listDatum.getTitle());
                    data1.put("desc", listDatum.getDescri());
                    data1.put("img_url", listDatum.getImg_url());
                    data1.put("html_url", listDatum.getHtml_url());
                    ArrayList list1 = new ArrayList();
                    list1.add(data1);
                    Map<String, Object> resultPicture = new LinkedHashMap<String, Object>();
                    resultPicture.put("is_display_title", listDatum.getIs_display_title());//是否显示标题
                    resultPicture.put("head_height", listDatum.getHead_height());
                    resultPicture.put("is_display_more", listDatum.getIs_display_more());
                    resultPicture.put("is_display_more_params", listDatum.getIs_display_more_params());
                    resultPicture.put("data", list1);
                    JSONObject jsonPicture = new JSONObject();
                    jsonPicture.put("section", resultPicture);
                    jsonPicture.put("style_name", listDatum.getStyle_name());
                    jsonPicture.put("number", listDatum.getNumber());
                    list.add(jsonPicture);
                }
            }
        }

        return list;
    }
    public BaseDictModel getCheckPersonInfo(String token,String version_code,String id_card,Integer is_fresh,String html_section_type){
        AppInfoModel.AppInfoBean appInfoBean= appInfoUtil.getAppInfo("cme");
        String target_id = appInfoBean.getTarget_id();
        BaseDictModel baseDictModel = new BaseDictModel();
        String h5_url = readMapper.getModuleTargetUrl(html_section_type);
        String sjson = appLoginFeign.getCheckPersonInfo(token, version_code, id_card, is_fresh, target_id, h5_url);
        if (!"".equals(sjson)) {
            JSONObject jo = JSONObject.parseObject(sjson);
            Integer code = jo.getInteger("code");
            baseDictModel.setMessage(jo.getString("message"));
            baseDictModel.setCode(code);
            baseDictModel.setBody(jo.get("body"));
        }else {
            baseDictModel.setCode(MessageUtil.code1);
            baseDictModel.setMessage(MessageUtil.code1Msg);
        }
        return baseDictModel;
    }


    public List<ModuleAreaDataModel> getModuleFirstCmeData(Map<String,Object> map){
        List<ModuleAreaDataModel> listData = readMapper.getModuleFirstCme(map);
        return listData;
    }

}

