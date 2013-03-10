package com.darylteo.promises.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.darylteo.promises.FailureHandler;
import com.darylteo.promises.PromiseHandler;

public class Promise {
  private com.darylteo.promises.Promise<Object> _promise = com.darylteo.promises.Promise.defer();

  public Promise then(final Function fulfilled) {
    return this.then(fulfilled, null);
  }

  public Promise then(final Function fulfilled, final Function rejected) {
    _promise.then(
        new PromiseHandler<Object, Object>() {
          @Override
          public Object handle(Object value) throws Exception {
            return invoke(fulfilled, value);
          }
        },
        new FailureHandler<Object>() {

          @Override
          public Object handle(Exception e) {
            return invoke(rejected, e);
          }
        }
        );

    return this;
  }

  public void fulfill(Object value) {
    _promise.fulfill(value);
  }

  public void reject(Object e) {
    _promise.reject(new Exception(e.toString()));
  }

  private Object invoke(Function function, Object... args) {
    if (function == null) {
      return null;
    }

    Context context = Context.enter();

    try {
      Scriptable scope = function.getParentScope();
      Scriptable that = context.newObject(scope);
      Object result = function.call(
          context, scope, that, args);
     
      return result;
    } finally {
      Context.exit();
    }
  }
}
