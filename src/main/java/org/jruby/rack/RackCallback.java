/*
 * Copyright (c) 2010 Engine Yard, Inc.
 * Copyright (c) 2007-2009 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jruby.rack;

import org.jruby.rack.RackResponse;

// TODO:  Rack Callback needs to do 2 things when going async... marke the HttpRequest as Async and manage application pools.
public interface RackCallback {
  void goAsync();
  void completeAsync(RackResponse resp);
}