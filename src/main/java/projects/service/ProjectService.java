package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) 
	{
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() 
	{
			return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectID) 
	{
		return projectDao.fetchProjectById(projectID).orElseThrow(() -> new NoSuchElementException
		("This project does not exist!"));
	}

	public void modifyProjectDetails(Project projectToUpdate) 
	{
		if (!projectDao.modifyProjectDetails(projectToUpdate))
		{
			throw new DbException("Project with ID " + projectToUpdate.getProjectId() + " does not exist!");
		}
	}

	public void deleteProject(Integer idToDelete) 
	{
		if (!projectDao.deleteProject(idToDelete))
		{
			throw new DbException("Project with ID " + idToDelete + " does not exist!");
		}
	}


}
