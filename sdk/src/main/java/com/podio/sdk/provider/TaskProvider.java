package com.podio.sdk.provider;

import com.podio.sdk.Filter;
import com.podio.sdk.Provider;
import com.podio.sdk.Request;
import com.podio.sdk.domain.ReferenceType;
import com.podio.sdk.domain.Task;
import com.podio.sdk.internal.Utils;

import java.util.Date;

/**
 * This class provides methods to access {@link Task} objects from the API.
 */
public class TaskProvider extends Provider {

    public static class GetTaskFilter extends Filter {

        public GetTaskFilter() {
            super("task/");
            this.fullView();
        }

        public GetTaskFilter app(long appId) {
            this.addQueryParameter("app", Long.toString(appId, 10));
            return this;
        }

        /**
         * @param isCompleted
         *         True to only return completed tasks, False to return open tasks.
         *
         * @return
         */
        public GetTaskFilter completed(boolean isCompleted) {
            this.addQueryParameter("completed", Boolean.toString(isCompleted));
            return this;
        }

        public GetTaskFilter completedBy(AuthType authType, long id) {
            this.addQueryParameter("completed_by", authType.getValue() + ":" + id);
            return this;
        }

        // TODO support SDK operation
        public GetTaskFilter completedOn() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        /**
         * @param authType
         * @param id
         *         You can reference the the active user (the user logged in) with id 0 assuming you
         *         are providing a USER {@link AuthType}
         *
         * @return
         */
        public GetTaskFilter createdBy(AuthType authType, long id) {
            this.addQueryParameter("created_by", authType.getValue() + ":" + id);
            return this;
        }

        public GetTaskFilter createdOn() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        public GetTaskFilter createdVia() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        /**
         * The from and to date the task should be due between. This method will only consider the
         * date part of the given from and to Date object, meaning that the time component is
         * disregarded. Thus the timezone in which these date represent is irrelevant.
         *
         * @param from
         * @param to
         *
         * @return
         */
        public GetTaskFilter dueDate(Date from, Date to) {
            this.addQueryParameter("due_date", Utils.formatDateUtc(from) + "-" + Utils.formatDateUtc(to));
            return this;
        }

        // TODO support SDK operation
        public GetTaskFilter externalId() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        // TODO support SDK operation
        public GetTaskFilter files() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        public GetTaskFilter grouping(Grouping group) {
            this.addQueryParameter("grouping", group.getValue());
            return this;
        }

        // TODO support SDK operation
        public GetTaskFilter label() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        public GetTaskFilter limit(int value) {
            this.addQueryParameter("limit", Integer.toString(value, 10));
            return this;
        }

        public GetTaskFilter offset(int value) {
            this.addQueryParameter("offset", Integer.toString(value, 10));
            return this;
        }

        public GetTaskFilter org(long orgId) {
            this.addQueryParameter("org", Long.toString(orgId, 10));
            return this;
        }

        /**
         * @param isReassigned
         *         set to true to only return tasks the active user has assigned to someone else,
         *         false to only return tasks that the active user has not assigned to someone
         *         else.
         *
         * @return
         */
        public GetTaskFilter reassigned(boolean isReassigned) {
            this.addQueryParameter("reassigned", Boolean.toString(isReassigned));
            return this;
        }

        // TODO support SDK operation
        public GetTaskFilter reference() {
            throw new UnsupportedOperationException("not implemented yet");
        }

        public GetTaskFilter responsible(long userId) {
            this.addQueryParameter("responsible", Long.toString(userId, 10));
            return this;
        }

        public GetTaskFilter sortBy(SortByType sortByType) {
            this.addQueryParameter("sort_by", sortByType.getValue());
            return this;
        }

        /**
         * If this filter isn't provided in
         *
         * @param isDescending
         *         true if tasks should be sorted descending, false otherwise
         *
         * @return
         */
        public GetTaskFilter sortDesc(boolean isDescending) {
            this.addQueryParameter("sort_desc", Boolean.toString(isDescending));
            return this;
        }

        public GetTaskFilter space(long spaceId) {
            this.addQueryParameter("space", Long.toString(spaceId, 10));
            return this;
        }

        private GetTaskFilter fullView() {
            this.addQueryParameter("view", "full");
            return this;
        }

        public enum SortByType {
            CREATED_ON("created_on"),
            COMPLETED_ON("completed_on"),
            RANK("rank");

            private String sortBy = null;

            SortByType(String sortBy) {
                this.sortBy = sortBy;
            }

            public String getValue() {
                return this.sortBy;
            }
        }

        public enum AuthType {

            USER("user"),
            APP("app");

            private String auth = null;

            AuthType(String auth) {
                this.auth = auth;
            }

            public String getValue() {
                return this.auth;
            }
        }

        /**
         * This enum is used to query the Podio API for a set of tasks ordered based on a certain
         * grouping
         */
        public enum Grouping {

            DUE_DATE("due_date"),
            CREATED_BY("created_by"),
            RESPONSIBLE("responsible"),
            APP("app"),
            SPACE("space"),
            ORG("org");

            private String group = null;

            Grouping(String group) {
                this.group = group;
            }

            public String getValue() {
                return this.group;
            }
        }
    }

    protected static class TaskFilter extends Filter {
        public TaskFilter() {
            super("task");
        }

        public TaskFilter withId(long taskId) {
            addPathSegment(Long.toString(taskId, 10));
            return this;
        }
    }

    /**
     * @param filter
     *
     * @return returns an array of {@link Task} domain objects from the API, depending on the given
     * filter.
     */
    public Request<Task[]> getTasks(GetTaskFilter filter) {
        return get(filter, Task[].class);
    }

    public Request<Task> getTask(long taskId) {
        return get(new TaskFilter().withId(taskId), Task.class);
    }

    public Request<Task> createTask(Task.CreateData createData) {
        return post(new TaskFilter(), createData, Task.class);
    }

    public Request<Task> updateTask(long taskId, Task.CreateData createData) {
        return put(new TaskFilter().withId(taskId), createData, Task.class);
    }

    public Request<Void> deleteTask(long taskId) {
        return delete(new TaskFilter().withId(taskId));
    }
}
