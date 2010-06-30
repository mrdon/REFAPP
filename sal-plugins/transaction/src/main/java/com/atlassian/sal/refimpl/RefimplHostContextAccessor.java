package com.atlassian.sal.refimpl;

import java.util.Map;

import com.atlassian.sal.spi.HostContextAccessor;

public class RefimplHostContextAccessor implements HostContextAccessor
{
    public Object doInTransaction(HostTransactionCallback callback)
    {
        return callback.doInTransaction();
    }

    public <T> Map<String, T> getComponentsOfType(Class<T> iface)
    {
        throw new UnsupportedOperationException("Use injection");
    }
}
