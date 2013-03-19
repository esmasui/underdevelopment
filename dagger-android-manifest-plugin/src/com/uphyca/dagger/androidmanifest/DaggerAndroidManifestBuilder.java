package com.uphyca.dagger.androidmanifest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;

import dagger.androidmanifest.ModuleGenerator;

public class DaggerAndroidManifestBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkXML(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkXML(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkXML(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	public static final String BUILDER_ID = "com.uphyca.dagger.androidmanifest.DaggerAndroidManifestBuilder";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkXML(IResource resource) {
		if (resource instanceof IFile
				&& resource.getName().equals("AndroidManifest.xml")
				&& resource.getParent().getLocation()
						.equals(resource.getProject().getLocation())) {

			File manifestFile = resource.getRawLocation().toFile();
			String moduleName = "ManifestModule";
			File outputDirectory = new File(resource.getProject()
					.getRawLocation().toFile(), "gen");

			try {
				ModuleGenerator.generate(manifestFile, moduleName,
						outputDirectory);
				getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (IOException e) {
				throw new RuntimeException("Unable to generate module.", e);
			} catch (SAXException e) {
				throw new RuntimeException("Unable to generate module.", e);
			} catch (ParserConfigurationException e) {
				throw new RuntimeException("Unable to generate module.", e);
			} catch (CoreException e) {
				throw new RuntimeException("Unable to generate module.", e);
			}
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
}
