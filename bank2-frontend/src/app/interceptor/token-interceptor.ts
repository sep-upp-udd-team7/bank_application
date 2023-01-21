import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpInterceptor,
  HttpEvent
} from '@angular/common/http';
import { AuthService } from '../service/auth.service';



@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(public auth: AuthService) { }
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (localStorage.getItem("jwt") != null) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${localStorage.getItem("jwt")}` 
        }
      });
    }
    return next.handle(request);
  }
}