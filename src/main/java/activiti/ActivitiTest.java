package activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class ActivitiTest {

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    @Test
    public void test() {
        //以下两种方式选择一种创建引擎方式：1.配置写在程序里 2.读对应的配置文件
        //1
        //testCreateProcessEngine();
        //2
        //estCreateProcessEngineByCfgXml();

        deployProcess();
        startProcess();
        queryTask();
    }

    /**
     * 测试activiti环境
     */
    @Test
    public void testCreateProcessEngine() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        cfg.setJdbcDriver("com.mysql.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/activiti");
        cfg.setJdbcUsername("root");
        cfg.setJdbcPassword("root");
        //配置建表策略
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine engine = cfg.buildProcessEngine();
    }

    /**
     * 根据配置文件activiti.cfg.xml创建ProcessEngine
     */
    @Test
    public void testCreateProcessEngineByCfgXml() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine engine = cfg.buildProcessEngine();
    }

    /**
     * 发布流程
     * RepositoryService
     */
    @Test
    public void deployProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment().name("leave");
        builder.addClasspathResource("test.bpmn");
        builder.deploy();
    }

    /**
     * 启动流程
     * RuntimeService
     */
    @Test
    public void startProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //可根据id、key、message启动流程
        runtimeService.startProcessInstanceByKey("myProcess_2");
    }

    /**
     * 查看任务
     * TaskService
     */
    @Test
    public void queryTask() {
        TaskService taskService = processEngine.getTaskService();
        //根据assignee(代理人)查询任务
        String assignee = "emp";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();

        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            Task task = tasks.get(i);

        }


        for (Task task : tasks) {
            System.out.println("taskId:" + task.getId() +
                    ",taskName:" + task.getName() +
                    ",assignee:" + task.getAssignee() +
                    ",createTime:" + task.getCreateTime());
        }
    }

    /**
     * 办理任务
     */
    @Test
    public void handleTask() {
        TaskService taskService = processEngine.getTaskService();
        //根据上一步生成的taskId执行任务
        String taskId = "22504";
        taskService.complete(taskId);
        System.out.println("任务完成");
    }

    /**
     * 历史活动查询
     */
    @Test
    public void historyActInstanceList(){
        String processInstanceId="10001";
        List<HistoricActivityInstance> list=processEngine.getHistoryService()
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .list();
        for (HistoricActivityInstance hai: list) {
            System.out.println("活动Id" + hai.getId());
            System.out.println("流程实例ID:"+hai.getProcessInstanceId());
            System.out.println("活动名称："+hai.getActivityName());
            System.out.println("办理人："+hai.getAssignee());
            System.out.println("开始时间："+hai.getStartTime());
            System.out.println("结束时间："+hai.getEndTime());
        }
    }

    /**
     * 历史流程实例查看
     * @throws Exception
     */
    @Test
    public void queryHistoricProcessInstance() throws Exception {
        // 获取历史流程实例的查询对象
        HistoricProcessInstanceQuery historicProcessInstanceQuery = processEngine.getHistoryService().createHistoricProcessInstanceQuery();
        // 设置查询参数
        historicProcessInstanceQuery
                //过滤条件
                .processDefinitionKey("myProcess_1")
                // 排序条件
                .orderByProcessInstanceStartTime().desc();
        // 执行查询
        List<HistoricProcessInstance> hpis = historicProcessInstanceQuery.list();
        // 遍历查看结果
        for (HistoricProcessInstance hpi : hpis) {
            System.out.print("pid:" + hpi.getId()+",");
            System.out.print("pdid:" + hpi.getProcessDefinitionId()+",");
            System.out.print("startTime:" + hpi.getStartTime()+",");
            System.out.print("endTime:" + hpi.getEndTime()+",");
            System.out.print("duration:" + hpi.getDurationInMillis()+",");
            System.out.println("vars:" + hpi.getProcessVariables());
        }
    }

    /**
     * 历史任务查询
     */
    @Test
    public void historyTaskList(){
        String processInstanceId="10001";
        List<HistoricTaskInstance> list=processEngine.getHistoryService() // 历史相关Service
                .createHistoricTaskInstanceQuery() // 创建历史任务实例查询
                .processInstanceId(processInstanceId) // 用流程实例id查询
                .finished() // 查询已经完成的任务
                .list();
        for(HistoricTaskInstance hti:list){
            System.out.println("任务ID:"+hti.getId());
            System.out.println("流程实例ID:"+hti.getProcessInstanceId());
            System.out.println("任务名称："+hti.getName());
            System.out.println("办理人："+hti.getAssignee());
            System.out.println("开始时间："+hti.getStartTime());
            System.out.println("结束时间："+hti.getEndTime());
            System.out.println("=================================");
        }
    }

    /**
     * 查询流程状态（正在执行 or 已经执行结束）
     */
    @Test
    public void processState(){
        ProcessInstance pi=processEngine.getRuntimeService() // 获取运行时Service
                .createProcessInstanceQuery() // 创建流程实例查询
                .processInstanceId("30005") // 用流程实例id查询
                .singleResult();
        if(pi!=null){
            System.out.println("流程正在执行！");
        }else{
            System.out.println("流程已经执行结束！");
        }
    }

}
