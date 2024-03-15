package projects;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.lang.*;
import java.math.BigDecimal;

import projects.dao.DbConnection;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class Projects {
	//Connection conn = DbConnection.getConnection();
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();

	//@formatter:off
	private static List <String> operations = List.of(
	"1. Add a Project"
			
	);
	
	//main method
	public static void main(String[] args) 
	{
		new Projects().processUserSelections();
	}
	
	//Process user selections w switch
	private void processUserSelections() 
	{
		boolean done = false;
		while(!done)
		{

			int selection = getUserSelection();
			switch(selection) 
			{
			  case 1:
				 createProject();
			    break;
			  case 2:
			    // code block
			    break;
			  case 3:
				    // code block
				    break;
			  case 4:
				    // code block
				    break;
			  case 5:
				    // code block
				    break;
			  default:
			    // code block
			}

		}
		
	}
	
	//create project
	private void createProject() {
		String projectName = getStringInput("Enter Project Name: ");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours: ");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours: ");
		Integer difficulty = getIntInput("Enter the project difficulty");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have created a project!");
		
	}
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input))
		{
			return null;
		}
		try
		{
			return new BigDecimal(input).setScale(2);
		}
		catch (NumberFormatException e)
		{
			throw new DbException(input + " is not a valid decimal number");
		}
	}

	//get user selection
	private int getUserSelection() 
	{
		printOpertations();
		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
	}
	
	//print operations
	private void printOpertations() 
	{
		System.out.println();
		System.out.println("These are the available selections. Press the Enter key to quit");
		operations.forEach(line -> System.out.println(" " + line));
	}
	
	//get integer input
	private Integer getIntInput(String prompt)
	{
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input))
		{
			return null;
		}
		try
		{
			return Integer.valueOf(input);
		}
		catch (NumberFormatException e)
		{
			throw new DbException(input + " is not a valid number");
		}
	}
	//get string input
	private String getStringInput(String prompt) 
	{
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();	
		
		if(input.isBlank())
		{
			return null;
		}
		else
		{
			return input.trim();
		}
	}
}
