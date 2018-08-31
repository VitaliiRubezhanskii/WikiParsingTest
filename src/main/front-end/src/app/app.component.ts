import {Component, OnInit} from '@angular/core';
import {ResponseService} from "./ResponseService";

export interface UrlResponse {
  Parents: {
      url:string;
      name:string;
      isReferenced:boolean;
    }[],
  Children: {
      url:string;
      name:string;
      isReferenced:boolean;
    }[]
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{

   url;
   response: UrlResponse;
   children;

  constructor(private responseService:ResponseService ){}

  ngOnInit():void {
  }

  doSearch(){
    console.log("doSearch Works!")
    console.log(this.url);
    this.responseService.getData(this.url).subscribe((data: UrlResponse) =>{
      this.response = data;
      this.children = data.Children;
      console.log(data);
    });

  }
}
