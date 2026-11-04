import {Component, EventEmitter, inject, Input, Output, viewChild} from '@angular/core';
import {Button} from "primeng/button";
import {Card} from "primeng/card";
import {Message} from "primeng/message";
import {DatePipe, NgIf} from "@angular/common";
import {MenuItem, MessageService, PrimeTemplate} from "primeng/api";
import {RouterLink} from "@angular/router";
import {Skeleton} from "primeng/skeleton";
import {TableModule} from "primeng/table";
import {Course} from '../../models/course.model';
import {CoursesService} from '../../services/courses.service';
import {Toast} from 'primeng/toast';
import {Menu} from 'primeng/menu';
import {Tag} from 'primeng/tag';
import {getVisibilityIcon, getVisibilityLabel} from '../../utils/course-visibility.util';

@Component({
  selector: 'app-courses-table',
  imports: [
    Button,
    Card,
    Message,
    PrimeTemplate,
    RouterLink,
    Skeleton,
    TableModule,
    DatePipe,
    Toast,
    Menu,
    Tag
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

  menu = viewChild.required<Menu>('menu');
  menuItems: MenuItem[] | undefined;

  openMenu(event: Event, course: Course) {
    event.stopPropagation();
    this.menuItems = [
      {
        label: 'Options',
        items: [
          {
            label: 'View',
            icon: 'pi pi-file',
            routerLink: [course.id, 'course-details']
          },
          {
            label: 'Delete',
            icon: 'pi pi-trash',
            command: () => this.onDeleteCourse(course.id)
          }
        ]
      }
    ];
    this.menu().toggle(event);
  }

  getMenuItems(course: Course): MenuItem[] {
    return [
      {
        label: 'Options',
        items: [
          {
            label: 'View',
            icon: 'pi pi-file',
            routerLink: [course.id, 'course-details']
          },
          {
            label: 'Delete',
            icon: 'pi pi-trash',
            command: () => this.onDeleteCourse(course.id)
          }
        ]
      }
    ];
  }

  onDeleteCourse(id: string) {
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

  protected readonly getVisibilityIcon = getVisibilityIcon;
  protected readonly getVisibilityLabel = getVisibilityLabel;
}
