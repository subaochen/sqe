/* Copyright 2005,2006 Sven Reimers, Florian Vogler
 *
 * This file is part of the Software Quality Environment Project.
 *
 * The Software Quality Environment Project is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * The Software Quality Environment Project is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nbheaven.sqe.informations.java;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.nbheaven.sqe.core.java.utils.FileObjectUtilities;
import org.nbheaven.sqe.core.java.utils.ProjectUtilities;
import org.nbheaven.sqe.informations.ui.spi.SQEInformationComponent;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author Sven Reimers
 */
class SQEInformationComponentImpl implements SQEInformationComponent {
    private final Project project;
    private JPanel panel = new JPanel();       
    
    public SQEInformationComponentImpl(Project project) {
        this.project = project;
    }

    @Override
    public Component getComponent() {
        panel.removeAll();
        JScrollPane pane = new JScrollPane(new InfoPanel(project));
        pane.setBorder(null);
        panel.setLayout(new BorderLayout());
        panel.add(pane, BorderLayout.CENTER);
        panel.revalidate();
        return panel;
    }

    @Override
    public void refresh() {        
        panel.removeAll();
        JScrollPane pane = new JScrollPane(new InfoPanel(project));
        pane.setBorder(null);
        panel.setLayout(new BorderLayout());
        panel.add(pane, BorderLayout.CENTER);
        panel.revalidate();
    }
    
    private class InfoPanel extends JPanel {
        private Project project;

        private InfoPanel(Project project) {
            this.project = project;
            init();
        }
        
        private void init() {
            this.setLayout(new BorderLayout());
            JTextPane pane = new JTextPane();
            pane.setContentType("text/html");
            pane.setText(getText());
            this.add(pane, BorderLayout.CENTER);
        }
        
        private String getText() {
            String begin = "<html>";
            String end = "</html>";
            
            String sources = getSourceDesc();
            String binaries = getBinariesDesc();
            
            return begin + sources + binaries + end;
        }
        
        private String getSourceDesc() {
            String source = "<p/><h2>Sources</h2><table><tr><th>Name</th><th>Location</th><th>Count</th></tr>";
            for(SourceGroup sourceGroup: ProjectUtilities.getJavaSourceGroups(project)) {
                FileObject fileObject = sourceGroup.getRootFolder();
                // XXX use XMLUtil.toElementContent
                source += "<tr><td>" + sourceGroup.getDisplayName() + "</td><td>" + FileUtil.getFileDisplayName(fileObject) +
                        "</td><td>" + FileObjectUtilities.collectAllJavaSourceFiles(fileObject).size() + "</td></tr>";
            }
            source += "</table>";
            return source;
        }

        private String getBinariesDesc() {
            String source = "<p/><h2>Binaries</h2><ul>";
            for (String root : ProjectUtilities.findBinaryRoots(project)) {
                // XXX use XMLUtil.toElementContent
                source += "<li>" + root + "</li>";
            }
            source += "</ul>";
            return source;
        }
    }
}
