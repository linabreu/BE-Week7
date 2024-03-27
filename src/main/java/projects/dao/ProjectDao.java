package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;


public class ProjectDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	//create the recipe
	public Project insertProject(Project project) 
	{
		// @formatter:off
		String sql = ""
		+"Insert into " + PROJECT_TABLE + " "
		+"(project_name, estimated_hours, actual_hours, difficulty, notes)"
		+ "VALUES "
		+"(?, ?, ?, ?, ?)";
		// @formatter:on
		
		try (Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
			}
			catch(Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch (SQLException e) 
		{
			throw new DbException(e);
		}
	}

	//fetch projects
	public List<Project> fetchAllProjects() 
	{
		String sql = "select * from " + PROJECT_TABLE + " order by project_id";
		//System.out.println(sql);
		
		try (Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql))
			{
				try(ResultSet rs = stmt.executeQuery(sql))
				{
					List<Project> projectList = new LinkedList<>();
					while (rs.next())
					{
					   projectList.add(extract(rs, Project.class));
					}
					return projectList;
				}
			}
			catch (Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch (Exception e)
		{
			throw new DbException(e);
		}
	}

	public Optional <Project> fetchProjectById(Integer projectID) 
	{
		//@formatter: off
		String sql = "select * from " + PROJECT_TABLE + " where project_id = ?";
		//@formatter: on
		try (Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try
			{
				Project returnProject = null;
				//return Optional.ofNullable(returnProject);
				try(PreparedStatement stmt = conn.prepareStatement(sql))
				{
					setParameter(stmt, 1, projectID, Integer.class);
					
					try (ResultSet rs = stmt.executeQuery())
					{
						if(rs.next())
						{
							returnProject = extract(rs, Project.class);
						}
					}
				}
				if(Objects.nonNull(returnProject))
				{
					returnProject.getMaterials().addAll(fetchMatieralsForProject(conn, projectID));
					returnProject.getSteps().addAll(fetchStepsForProject(conn, projectID));
					returnProject.getCategories().addAll(fetchCategoriesForProject(conn, projectID));	
				}
				commitTransaction(conn);
				return Optional.ofNullable(returnProject);
			}

			catch (Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch (Exception e)
		{
			throw new DbException(e);
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectID) throws SQLException
	{
		{
			//@formatter:off
			String sql = ""
			+ "Select * from " + STEP_TABLE + " where project_id = ?";
			//@formatter: on
			try (PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, projectID, Integer.class);
				try (ResultSet rs = stmt.executeQuery())
				{
					List<Step> stepsList = new LinkedList<Step>();
					while (rs.next())
					{
						stepsList.add(extract(rs, Step.class));
					}
					return stepsList;
				}
			}
		}
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectID) throws SQLException
	{
		{
			//@formatter: off
			String sql = ""
			+ "select c.* from " + CATEGORY_TABLE + " c "
			+ "join " + PROJECT_CATEGORY_TABLE + " pc using (category_id)"
			+ "where project_id = ? ";
			//formatter: on
	
			try (PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, projectID, Integer.class);
				try(ResultSet rs = stmt.executeQuery())
				{
					List<Category> categoriesList = new LinkedList<Category>();
					while (rs.next())
					{
						categoriesList.add(extract (rs, Category.class));
					}
					return categoriesList;
				}
			} 

		}
	}

	private List<Material> fetchMatieralsForProject(Connection conn, Integer projectID) throws SQLException
	{
		{
			//@formatter: off
				String sql = ""
				+ "select * from " + MATERIAL_TABLE + " where project_id = ?";
			//@formatter: on
			try (PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, projectID, Integer.class);
				try (ResultSet rs = stmt.executeQuery())
				{
					List <Material> materialsList = new LinkedList<Material>();
					while (rs.next())
					{
						materialsList.add(extract(rs, Material.class));
					}
					return materialsList;
				}
			}
		}
	}

	//update a project
	public boolean modifyProjectDetails(Project projectToUpdate) 
	{
		//@formatter: off
		String sql = ""
		+ "update " + PROJECT_TABLE + " SET "
		+ "project_name = ?, " 
		+ "estimated_hours = ?, "
		+ "actual_hours = ?, "
		+ "difficulty = ?, " 
		+ "notes = ? "
		+ "where project_id = ? ";
		//@formatter: on
		//System.out.println(sql);
		
		try (Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, projectToUpdate.getProjectName(), String.class);
				setParameter(stmt, 2, projectToUpdate.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, projectToUpdate.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, projectToUpdate.getDifficulty(), Integer.class);
				setParameter(stmt, 5, projectToUpdate.getNotes(), String.class);
				setParameter(stmt, 6, projectToUpdate.getProjectId(), Integer.class);
				
				//System.out.println(stmt);
				
				boolean sucessfullyModified = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				return sucessfullyModified;
				
			}
			catch(Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch (SQLException e)
		{
			throw new DbException(e);
		}
	}

	//delete project
	public boolean deleteProject(Integer idToDelete) 
	{
	 //@formatter:off
		String sql = "delete from " + PROJECT_TABLE + " where project_id = ?";
	//@formatter:off
		try (Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, idToDelete, Integer.class);
				
				boolean sucessfullyDeleted = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				return sucessfullyDeleted;
			}
			catch (Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch (SQLException e)
		{
			throw new DbException(e);
		}
	}
}
