/*
 * Copyright (c) 2020. Vasileios Balafas
 */

public interface Task extends Runnable {

    public String getTaskId();

    public Integer getOrder();

    public Integer getExitcode();


}