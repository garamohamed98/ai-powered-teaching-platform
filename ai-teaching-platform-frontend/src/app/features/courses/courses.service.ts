import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Course} from './models/course.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {

  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/course';

  constructor() {}

  getCourses() {
    return this.http.get<Course[]>(this.baseUrl);
  }

  getCourseDetails(courseId: string) {
    return this.http.get<Course>(this.baseUrl +"/" +courseId);
  }

  updateCourseContent(courseId: string, content: string) {
    const url = `${this.baseUrl}/${courseId}/content`;
    const body = {
      content: content
    };
    console.log("update course content course id: ", courseId,
      " content: ",content,
      " in url: ",url,
      " with body: ",body
      );
    return this.http.patch<Course>(url,body);
  }

  createCourse(title:string) : Observable<HttpResponse<Course>>{
    const body = {
      title: title,
    }
    return this.http.post<Course>(this.baseUrl, body,{
      observe: 'response'
    });
  }
}
