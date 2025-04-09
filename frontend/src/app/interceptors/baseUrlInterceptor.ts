import { HttpEvent, HttpHandler, HttpHandlerFn, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";


export function baseUrlInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {

    const apiReq = req.clone({ url: `api/${req.url}` });
    
    // const apiReq = req.clone({ url: `${environment.apiUrl}/${req.url}` });
    
    return next(req);
  }