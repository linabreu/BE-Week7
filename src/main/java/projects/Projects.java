package projects;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.math.BigDecimal;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class Projects {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project currentProject;


	//@formatter:off
	private static List <String> operations = List.of(
	"1. Add a Project", 
	"2. List Projects",
	"3. Select a Project",
	"4. Update project details",
	"5. Delete a project"
			
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
			try
			{
				int selection = getUserSelection();
				switch(selection) 
				{
				  case 1:
					 createProject();
				    break;
				  case 2:
				    listProjects();
				    break;
				  case 3:
					  selectProject(selection);
					break;
				  case 4:
					  updateProjectDetails();
					    break;
				  case 5:
					    deleteProject();
					    break;
				  default:
					System.out.println("Thank you for using the project service app! See you next time!");
					done = true;
				}
	
			}
			catch (Exception e)
			{
				System.out.println("Error! " + e + " Please try again!");
			}
		}
		
	}
	//delete method
	private void deleteProject() {
		listProjects();
		
		Integer idToDelete = getIntInput("Enter the id of the project to be deleted");
		projectService.deleteProject(idToDelete);
		System.out.println("Project id: " + idToDelete + " sucessfully deleted!");
		
		if(Objects.nonNull(currentProject) && currentProject.getProjectId().equals(idToDelete))
		{
			currentProject = null;
		}

		
	}
    //update method
	private void updateProjectDetails() {
		if (Objects.isNull(currentProject))
		{
			System.out.println("\nPlease select a project first!");
			return;
		}
			System.out.println("Current project settings:");
			
			String projectName = getStringInput("Enter Project Name [" + currentProject.getProjectName()+ "]");
			BigDecimal projectEstimatedHours = getDecimalInput("Enter Estimated Hours [" + currentProject.getEstimatedHours() +"]");
			BigDecimal projectActualHours = getDecimalInput("Enter Actual Hours [" + currentProject.getEstimatedHours()+ "] ");
			Integer projectDifficulty = getIntInput("Enter Difficulty [" + currentProject.getDifficulty() + "] ");
			String projectNotes = getStringInput("Enter Notes [" + currentProject.getNotes()+ "] ");
			
			Project projectToUpdate = new Project();
			
			projectToUpdate.setProjectName(Objects.isNull(projectName)
					? currentProject.getProjectName(): projectName);
			
			projectToUpdate.setEstimatedHours(Objects.isNull(projectEstimatedHours)
					? currentProject.getEstimatedHours(): projectEstimatedHours);
			
			projectToUpdate.setActualHours(Objects.isNull(projectEstimatedHours)
					? currentProject.getActualHours(): projectActualHours);
			
			projectToUpdate.setDifficulty(Objects.isNull(projectDifficulty)
					? currentProject.getDifficulty(): projectDifficulty);
			
			projectToUpdate.setNotes(Objects.isNull(projectNotes)
					? currentProject.getNotes(): projectNotes);
			
			projectToUpdate.setProjectId(currentProject.getProjectId());
			
			projectService.modifyProjectDetails(projectToUpdate);
			
			currentProject = projectService.fetchProjectById(projectToUpdate.getProjectId());
		
	}

	//select project
	private void selectProject(Integer projectID) {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		currentProject = null; 
		currentProject = projectService.fetchProjectById(projectId);
		
	}

	//list all projects
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");
		projects.forEach(project -> System.out.println(project.getProjectId() + ". " + project.getProjectName()));
		
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
		
		if(Objects.isNull(currentProject))
		{
			System.out.println("\nYou are not working with a project!");
		}
		else
		{
			System.out.println("\nYou are working with project: " + currentProject);
		}
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
