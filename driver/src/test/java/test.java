import org.neo4j.driver.v1.*;
import java.util.Stack;
import java.util.Vector;


public class test {
	
	public static void main(String [] args)
	{
		Driver driver = null;
		Session session = null;
		driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic( "neo4j", "123456" ) );
		session = driver.session();

		String id = "R-HSA-69580";
		
		
		query(session, id);
		//session.run( "CREATE (a:Person {name:'Arthur', title:'King'})" );

		

		session.close();
		driver.close();
	}
	
	public static void query(Session session, String id)
	{
		String DOI = null;
		
		String Label = get_label(session, id);
		
		
//		while(Label != "Pathway")
//		{
//			
//		}
		
		Vector<String> doi_list = get_ancestor_DOI(session, id);
		for(int i = 0; i<doi_list.size(); i++)
		{
			System.out.println(doi_list.get(i));
		}
	}
	
	public static String get_label(Session session, String id)
	{
		String query = "MATCH (object{stId:\""+ id + "\"}) RETURN object.simpleLabel AS LABEL";
		System.out.println(query);
		StatementResult result = session.run( query );
		while ( result.hasNext() )
		{
		    Record record = result.next();
		    return record.get( "LABEL" ).asString();
		}
		return null;
	}
	
//	public static recursive_up(Session session, String id)
//	{
//		
//	}
	
	public static Vector<String> get_ancestor_DOI(Session session, String id)
	{
		String DOI = null;
		String ID = id;
		
		String query = "MATCH (pathway{stId:\""+ ID +"\"}) RETURN pathway.doi AS DOI";
		StatementResult result = session.run( query );
		Stack <String> stack = new Stack<String>();
		Vector <String> doi_str = new Vector<String>();
		
		while ( result.hasNext() )
		{
		    Record record = result.next();
		    DOI = record.get( "DOI" ).asString();
		}
		if(DOI.equals("null"))
			stack.push(ID);
		else
			doi_str.add(DOI);
		
		while(!stack.isEmpty())
		{
			String temp_id = stack.pop();
			
			query = "MATCH (pathway{stId:\""+ temp_id + "\"})<-[:hasEvent]-(event) RETURN event.doi AS DOI, event.stId AS ID";
			System.out.println(query);
			result = session.run(query);
			while ( result.hasNext() )
			{
			    Record record = result.next();
			    DOI = record.get( "DOI" ).asString();
			    if(DOI.equals("null"))
			    	stack.push(record.get( "ID" ).asString());
			    else
			    	doi_str.add(DOI);
			}
		}
		return doi_str;
		
	}

}
