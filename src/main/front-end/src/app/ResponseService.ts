import { Injectable } from '@angular/core';




import {ResponseObject} from "./ResponseObject";
import {HttpClient} from "@angular/common/http";


@Injectable()
export class ResponseService {

     url="http://localhost:8080/parse?parseUrl=";
     constructor(private http:HttpClient){}
     getData(prefix:string){return this.http.get(this.url+prefix);}
}
