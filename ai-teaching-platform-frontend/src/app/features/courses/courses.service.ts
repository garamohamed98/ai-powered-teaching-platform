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
    console.log("fetching courses");
    return this.http.get<Course[]>(this.baseUrl);
  }

  getCourseDetails(courseId: string) {
    console.log("fetching course details")
    return this.http.get<Course>(this.baseUrl +"/" +courseId);
  }

  updateCourseContent(courseId: string, content: string) {
    const url = `${this.baseUrl}/${courseId}/content`;
    const body = {
      content: content
    };
    console.log("updating course content course id: ", courseId,
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
    console.log("creating course: ",body);
    return this.http.post<Course>(this.baseUrl, body,{
      observe: 'response'
    });
  }

  deleteCourse(id: number) {
    console.log("deleting course id: ",id);
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
