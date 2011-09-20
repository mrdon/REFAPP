package com.atlassian.streams.refapp;

import java.net.URI;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.StreamsEntry.ActivityObject;
import com.atlassian.streams.api.StreamsException;
import com.atlassian.streams.api.StreamsFeed;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.NonEmptyIterables;
import com.atlassian.streams.spi.CancellableTask;
import com.atlassian.streams.spi.StreamsActivityProvider;
import com.atlassian.streams.spi.StreamsI18nResolver;

import com.google.common.collect.ImmutableList;

import org.joda.time.DateTime;

import static com.atlassian.streams.api.ActivityObjectTypes.comment;
import static com.atlassian.streams.api.ActivityVerbs.post;
import static com.atlassian.streams.api.common.Option.none;
import static com.atlassian.streams.api.common.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

public class RefappStreamsActivityProvider implements StreamsActivityProvider
{
    private final ApplicationProperties applicationProperties;
    private final StreamsI18nResolver i18nResolver;
    private final RefappRenderer refappRenderer;

    public RefappStreamsActivityProvider(ApplicationProperties applicationProperties,
                                         StreamsI18nResolver i18nResolver,
                                         RefappRenderer refappRenderer)
    {
        this.applicationProperties = checkNotNull(applicationProperties, "applicationProperties");
        this.i18nResolver = checkNotNull(i18nResolver, "i18nResolver");
        this.refappRenderer = checkNotNull(refappRenderer, "refappRenderer");
    }

    public CancellableTask<StreamsFeed> getActivityFeed(ActivityRequest activityRequest) throws StreamsException
    {
        return new CancellableTask<StreamsFeed>()
        {
            public StreamsFeed call() throws Exception
            {
                return new StreamsFeed("Activity Stream for RefApp", getEntries(), none(String.class));
            }

            public Result cancel()
            {
                return Result.CANCELLED;
            }
        };
    }

    private Iterable<StreamsEntry> getEntries()
    {
        return ImmutableList.of(createEntry(1, "did something"),
                                createEntry(2, "finished another thing"));
    }

    private StreamsEntry createEntry(int id, String title)
    {
        return new StreamsEntry(StreamsEntry.params()
                .id(URI.create(applicationProperties.getBaseUrl() + "/refapp-streams/" + id))
                .postedDate(new DateTime())
                .applicationType("com.atlassian.refimpl")
                .alternateLinkUri(URI.create(applicationProperties.getBaseUrl() + "/refapp-streams/" + id))
                .authors(NonEmptyIterables.from(ImmutableList.of(new UserProfile.Builder("admin").build())).get())
                .verb(post())
                .addActivityObject(new ActivityObject(ActivityObject.params()
                        .id("activity-object-" + id)
                        .activityObjectType(comment())
                        .title(some(title))))
                .renderer(refappRenderer), i18nResolver);
    }

}
