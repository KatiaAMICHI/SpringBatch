package com.jump.task;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.cloud.task.batch.partition.CommandLineArgsProvider;
import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;

import java.util.ArrayList;
import java.util.List;

public class AppendingCommandLineArgsProvider implements TaskExecutionListener, CommandLineArgsProvider {

	private List<String> args = new ArrayList<>();

	@Override
	public void onTaskStartup(TaskExecution taskExecution) {
		args.addAll(taskExecution.getArguments());
	}

	@Override
	public void onTaskEnd(TaskExecution taskExecution) {

	}

	@Override
	public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {

	}

	public void addCommandLineArgs(List<String> args) {
		this.args.addAll(args);
	}

	@Override
	public List<String> getCommandLineArgs(ExecutionContext executionContext) {
		System.out.println(">> Command Line Args = ");

		for (String arg : args) {
			System.out.println(arg);
		}

		return args;
	}
}
