import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {Message} from "primeng/message";
import {NgIf} from "@angular/common";
import {MessageService, PrimeTemplate} from "primeng/api";
import {RouterLink} from "@angular/router";
import {Skeleton} from "primeng/skeleton";
import {TableModule} from "primeng/table";
import {Course} from '../../models/course.model';
import {CoursesService} from '../../courses.service';

@Component({
  selector: 'app-courses-table',
    imports: [
        Button,
        Card,
        Message,
        NgIf,
        PrimeTemplate,
        RouterLink,
        Skeleton,
        TableModule
    ],
  templateUrl: './courses-table.component.html',
  styleUrl: './courses-table.component.scss'
})
export class CoursesTableComponent {
  private courseService = inject(CoursesService);
  private messageService = inject(MessageService);

  @Input({required:true}) maxRows!: number;
  @Input({required:true}) courses!: Course[];
  @Input({required:true}) loading!: boolean;

  @Output() courseDeleted = new EventEmitter<void>();

  onDeleteCourse(id: number) {
    this.courseService.deleteCourse(id).subscribe({
      next: (res) => {
        console.log("Deleted Course event triggered");
        this.messageService.add({
          severity: 'success',
          summary: 'Course deleted successfully',
        });
        this.courseDeleted.emit();
      }
    })
  }

}
