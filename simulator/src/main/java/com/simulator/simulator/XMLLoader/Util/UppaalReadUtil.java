package com.simulator.simulator.XMLLoader.Util;


import com.simulator.simulator.XMLLoader.System.*;
import com.simulator.simulator.XMLLoader.task.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class UppaalReadUtil {
    public static TaskDiagram uppaalTaskReader(){
        InputStream in = UppaalReadUtil.class.getClassLoader().getResourceAsStream("TaskGraph.xml");
        SAXReader reader = new SAXReader();
        //任务实例列表
        LinkedList<Task> GlobalTaskList = new LinkedList<>();
//        GlobalTaskList.add(new Task());
        //任务模板列表
        LinkedList<TaskModel> modelTaskList = new LinkedList<>();
        //data模板列表
        LinkedList<Data> dataModelList = new LinkedList<>();
        try {
            Document read = reader.read(in);
            Iterator<Element> iterator = read.getRootElement().element("task_graph").element("task_list").elementIterator();
            //遍历所有task，获取task---------------------------
            while (iterator.hasNext()){
                Element task = iterator.next();
                /**
                 * <task name="Task0">
                 * 		<properties cost="51536" instCnt="1" jobTypeId="391" knrlType="2" priority="0"/>
                 * 		<proc_items>
                 * 			<proc_item data_name="Data0" id="0" mov_dir="1">
                 * 				<data_inst job_inst_idx="0" total_size="15360">
                 * 					<data_inst_idx value="0" />
                 * 				</data_inst>
                 * 			</proc_item>
                 * 		</proc_items>
                 */
                LinkedList<Task> TaskList = new LinkedList<>();
                /**
                 * 任务属性：定义统一的任务属性
                 *         各个任务实例的区分是由DataForTask的赋值决定
                 */
                Element properties = task.element("properties");
                String taskName = (String)task.attribute("name").getData();
                int cost = Integer.parseInt((String) properties.attribute("cost").getData());
                int instCnt =  Integer.parseInt((String)properties.attribute("instCnt").getData());
                int knrlType =  Integer.parseInt((String)properties.attribute("knrlType").getData());
                int priority =  Integer.parseInt((String)properties.attribute("priority").getData());
                //添加模板
                TaskModel modelTask = new TaskModel(taskName, knrlType, instCnt, cost, priority);
                modelTaskList.add(modelTask);
                //添加实例(按照实例数量）
                for(int i = 0;i < instCnt;i++){
                    int job_inst_idx = i;
                    Task task1 = new Task(taskName, knrlType, instCnt, cost, priority,job_inst_idx);
                    TaskList.add(task1);
                }

                //任务输入输出
                Element proc_items = task.element("proc_items");
                Iterator<Element> proc_itemsIterator = proc_items.elementIterator();
                /**
                 * 一个输入输出数据
                 * <proc_item data_name="Data0" id="0" mov_dir="1">
                 *     一个Data实例
                 *     <data_inst job_inst_idx="0" total_size="15360">
                 *          任务实例中的一个数据
                 *          <data_inst_idx value="0" />
                 */
                while(proc_itemsIterator.hasNext()){
                    //一个Data模板（有多个实例）
                    Element proc_item = proc_itemsIterator.next();
                    String data_name = (String)proc_item.attribute("data_name").getData();
                    int mov_dir = Integer.parseInt((String)proc_item.attribute("mov_dir").getData());
                    DataForTask dataModel = new DataForTask(data_name, mov_dir);
                    if(mov_dir == 0){
                        modelTask.getDataIn().add(dataModel);
                    }else{
                        modelTask.getDataOut().add(dataModel);
                    }

                    //创建Data实例
                    Iterator<Element> data_instIterator = proc_item.elementIterator();
                    while(data_instIterator.hasNext()){
                        /**
                         * 一个任务实例包含：
                         *      统一属性：data_name、mov_dir、id
                         *      实例属性：job_inst_idx，total_size，data_inst_idx
                         * 通过job_inst_idx赋值给对应的任务实例
                         */
                        Element data_inst = data_instIterator.next();
                        int job_inst_idx = Integer.parseInt((String) data_inst.attribute("job_inst_idx").getData());
                        int total_size = Integer.parseInt((String) data_inst.attribute("total_size").getData());

                        LinkedList<Integer> data_inst_idx = new LinkedList<>();
                        Iterator<Element> data_inst_idxIterator = data_inst.elementIterator();
                        while (data_inst_idxIterator.hasNext()){
                            Element data_inst_idx_1 = data_inst_idxIterator.next();
                            int value = Integer.parseInt((String)data_inst_idx_1.attribute("value").getData());
                            data_inst_idx.add(value);

                            //创建多个dataIns
                            DataInstance dataInstance = new DataInstance(data_name, mov_dir, job_inst_idx, total_size, value);
                            //把数据实例放入对应的任务实例
                            Task taskIns = TaskList.get(job_inst_idx);
                            if(mov_dir == 0){
                                taskIns.getDataInsIn().add(dataInstance);
                            }else {
                                taskIns.getDataInsOut().add(dataInstance);
                            }
                        }
                        DataForTask dataForTask = new DataForTask(data_name, mov_dir, job_inst_idx, total_size, data_inst_idx);
                        Task taskIns = TaskList.get(job_inst_idx);
                        if(mov_dir == 0){
                            taskIns.getDataIn().add(dataForTask);
                        }else {
                            taskIns.getDataOut().add(dataForTask);
                        }
//                        if(taskIns.name.equals("Task1")) System.out.println(taskIns.getDataIn());
                    }
                }
                GlobalTaskList.addAll(TaskList);
            }

            //获取Data-----------
            Iterator<Element> dataIterator = read.getRootElement().element("data_info").elementIterator();
            while(dataIterator.hasNext()){
                /**
                 *  <data consumer_count="1" is_global="False" mem_type="1" name="Data0" producer="Task0">
                 *      size是实例数据，可以有多个
                 *      <size ref_count="1" value="15360" />
                 *  <consumer name="Task2" />
                 *
                 */
                Element dataElement = dataIterator.next();
                int consumer_count = Integer.parseInt((String) dataElement.attribute("consumer_count").getData());
                Boolean is_global = (Boolean) dataElement.attribute("is_global").getData().equals("True");
                int mem_type = Integer.parseInt((String) dataElement.attribute("mem_type").getData());
                String name = (String) dataElement.attribute("name").getData();
                String producer = (String) dataElement.attribute("producer").getData();

                //获取消费者
                Element consumer = dataElement.element("consumer");
                String consumerName = null;
                if(consumer!= null){consumerName = (String)consumer.attribute("name").getData();}

                Data data = new Data(consumer_count, is_global, mem_type, name, producer, consumerName);
                dataModelList.add(data);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        TaskDiagram taskDiagram = new TaskDiagram(GlobalTaskList, modelTaskList, dataModelList);
        return  taskDiagram;
    }

    public static ComponentStructure uppaalComponentReader(){
        InputStream inHardware = UppaalReadUtil.class.getClassLoader().getResourceAsStream("HardWare.xml");
        //实例化SaxReader
        SAXReader reader = new SAXReader();
        //module列表
        LinkedList<SubSystem> subSystems = new LinkedList<SubSystem>();
        //topModule存放
        MainSystem mainSystem = new MainSystem();
        //使用Reader读取
        try {
            Document read = reader.read(inHardware);
            //获取Root
            Element root = read.getRootElement();
            //获取一个模型
            Iterator<Element> moduleIterator = root.elementIterator();
            while (moduleIterator.hasNext()){
                //<module is_top="0" name="SUB_SYS">
                Element module = moduleIterator.next();
                int is_top = Integer.parseInt(module.attributeValue("is_top"));
                String moduleName = module.attributeValue("name");

                /**
                 * 获取element
                 *      由于connection和export都是upp定好的，所以这里不做获取
                 */
                LinkedList<ModuleElement> moduleElements = new LinkedList<ModuleElement>();
                Iterator<Element> elementIterator = module.element("elements").elementIterator();
                while(elementIterator.hasNext()){
                    //<element count="1" in_num="3" name="BUS" out_num="2" type="AXIBus"/>
                    Element element = elementIterator.next();
                    //取值为1
                    int count = 1;
//                    System.out.println(element.attributeValue("out_num"));

                    int in_num = Integer.parseInt(element.attributeValue("in_num"));
                    String name = element.attributeValue("name");
                    int out_num = Integer.parseInt(element.attributeValue("out_num"));
                    String type = element.attributeValue("type");

                    ModuleElement moduleElement = new ModuleElement(count, in_num,name,out_num, type);
                    moduleElements.add(moduleElement);
                }
                //根绝is_top创建module
                if(is_top == 0){
                    SubSystem subSystem = new SubSystem(moduleElements, moduleName);
                    subSystems.add(subSystem);
                }else {
                    //如果是topModule还需要获取component
                    Iterator<Element> componentsIterator = module.element("components").elementIterator();
                    LinkedList<Component> components = new LinkedList<Component>();
                    while (componentsIterator.hasNext()) {
                        //<component count="1" is_top="0" name="SUB_SYS0" type="SUB_SYS"/>
                        Element componentElement = componentsIterator.next();
                        String componentName = componentElement.attributeValue("name");
                        String componentType = componentElement.attributeValue("type");
                        Component component = new Component(componentName, componentType);
                        components.add(component);
                    }
                    MainSystem mainSystem1 = new MainSystem(moduleElements, moduleName, components);
                    mainSystem = mainSystem1;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        ComponentStructure componentStructure = new ComponentStructure(subSystems, mainSystem);
        return componentStructure;
    }
}
