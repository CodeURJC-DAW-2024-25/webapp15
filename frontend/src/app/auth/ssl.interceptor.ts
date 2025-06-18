import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';

@Injectable()
export class SslInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const sslReq = req.clone({
      setHeaders: {
        'X-SSL-Header': 'true'
      }
    });
    return next.handle(sslReq);
  }
}