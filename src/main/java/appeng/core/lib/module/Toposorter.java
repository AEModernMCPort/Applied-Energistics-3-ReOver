
package appeng.core.lib.module;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class Toposorter
{
	public static class Graph<T>
	{

		public class Node
		{

			private T data;
			private String name;
			private Set<Node> deps;
			private Set<Node> dependOnMe;
			private boolean isBeingChecked; // Toposorting helper

			private Node( String name, T data )
			{
				this.name = name;
				this.data = data;
				deps = Sets.newHashSet();
				dependOnMe = Sets.newHashSet();
			}

			public String getName()
			{
				return name;
			}

			public T getData()
			{
				return data;
			}

			public Set<Node> getDependencies()
			{
				return deps;
			}

			public Set<Node> getWhatDependsOnMe()
			{
				return dependOnMe;
			}

			public Node dependOn( Node dep )
			{
				deps.add( dep );
				dep.dependOnMe.add( this );
				return this;
			}

			public Node dependencyOf( Node dep )
			{
				dependOnMe.add( dep );
				dep.deps.add( this );
				return this;
			}

			public boolean isDependencyOf( Node dep )
			{
				return dependOnMe.contains( dep );
			}

			public boolean isDependingOn( Node dep )
			{
				return deps.contains( dep );
			}

			private void mark()
			{
				isBeingChecked = true;
			}

			private void unmark()
			{
				isBeingChecked = false;
			}

			private boolean isMarked()
			{
				return isBeingChecked;
			}

			@Override
			public int hashCode()
			{
				return getName().hashCode();
			}

			@Override
			public boolean equals( Object o )
			{
				if( o == null )
				{
					return false;
				}
				if( !( o instanceof Graph.Node ) )
				{
					return false;
				}
				return ( (Node) o ).getName().equals( this.getName() ) && ( (Node) o ).getData().equals( this.getData() );
			}
		}

		private Map<String, Node> nodes;

		public Graph()
		{
			nodes = Maps.newHashMap();
		}

		public Node addNewNode( String name, T data )
		{
			Node result = new Node( name, data );
			nodes.put( name, result );
			return result;
		}

		public Node getNode( String name )
		{
			return nodes.get( name );
		}

		public Collection<Node> getAllNodes()
		{
			return nodes.values();
		}

		public boolean hasNode( String name )
		{
			return nodes.containsKey( name );
		}
	}

	public static <T> List<T> toposort( Graph<T> graph ) throws SortingException
	{
		List<T> res = Lists.newArrayList();
		LinkedList<String> debugger = Lists.newLinkedList();
		for( Graph<T>.Node node : graph.getAllNodes() )
		{
			inspectNode( node.getName(), graph, res, debugger );
		}
		return res;
	}

	private static <T> void inspectNode( String name, Graph<T> graph, List<T> res, LinkedList<String> debugger ) throws SortingException
	{
		Graph<T>.Node node = graph.getNode( name );
		if( node.isMarked() )
		{
			throw new SortingException( name, debugger );
		}
		debugger.push( name );
		node.mark();
		for( Graph<T>.Node subNode : node.getDependencies() )
		{
			inspectNode( subNode.getName(), graph, res, debugger );
		}
		if( !res.contains( node.getData() ) )
		{
			res.add( node.getData() );
		}
		node.unmark();
		debugger.pop();
	}

	public static class SortingException extends Exception
	{

		private String nodeName;
		private List<String> visitOrder;

		public SortingException( String node, List<String> visitOrder )
		{
			nodeName = node;
			this.visitOrder = visitOrder;
		}

		public String getNode()
		{
			return nodeName;
		}

		public List<String> getVisitedNodes()
		{
			return visitOrder;
		}
	}

}
