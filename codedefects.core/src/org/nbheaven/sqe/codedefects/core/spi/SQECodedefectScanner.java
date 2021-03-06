/* Copyright 2005,2006,2015 Sven Reimers, Florian Vogler
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
package org.nbheaven.sqe.codedefects.core.spi;

import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Sven Reimers
 */
public final class SQECodedefectScanner {

    private static final RequestProcessor SCANNER_QUEUE = new RequestProcessor("SQECodedefectScanner",3);

//    private static SQECodedefectScanner INSTANCE = new SQECodedefectScanner();
    private SQECodedefectScanner() {
    }

    public static abstract class Job implements Runnable {

        private ProgressHandle progressHandle;

        protected Job() {
        }

        protected abstract String getDisplayName();

        protected final ProgressHandle getProgressHandle() {
            if (null == progressHandle) {
                progressHandle = ProgressHandle.createHandle(getDisplayName());
            }
            return progressHandle;
        }

        @Override
        public final void run() {
            try {
                scan();
            } finally {
                postScan();
            }
        }

        protected abstract void scan();

        protected void postScan() {
            getProgressHandle().finish();
        }

        protected void preScan() {
            getProgressHandle().start();
            getProgressHandle().switchToIndeterminate();
            getProgressHandle().progress("Scheduled for execution");
        }
    }

    public static void postAndWait(Job job) {
        RequestProcessor.Task task = SCANNER_QUEUE.create(job);
        job.preScan();
        SCANNER_QUEUE.post(task);
        task.waitFinished();
    }

    public static void post(Job job) {
        job.preScan();
        SCANNER_QUEUE.post(job);
    }
}
