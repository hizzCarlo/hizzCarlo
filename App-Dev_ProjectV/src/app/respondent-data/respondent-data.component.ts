import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../services/user.service';
import { LoginService } from '../users/login/login.service'; // Import LoginService

@Component({
  selector: 'app-respondent-data',
  templateUrl: './respondent-data.component.html',
  styleUrls: ['./respondent-data.component.css']
})
export class RespondentDataComponent {


  StudentArray : any[] = [];
  isResultLoaded = false;
  isUpdateFormActive = false;

  employedCount = 0;
  unemployedCount = 0;
  seekingEmploymentCount = 0;

  respondentCourse: string = "";
  respondentOccupation: string = "";
  respondentStatus: string = "";
  respondentName: string = "";
  respondentEmail: string = "";
  currentStudentID = "";
  searchText: string = '';
  searchSurvey: string = ''; // Add this line
  selectedCourse: string = 'ALL CATEGORIES'; // Default value for the dropdown

  constructor(private http: HttpClient, private userService: UserService, private loginService: LoginService) 
  {
    this.checkAdminAndGetAllStudents();
  }
  ngOnInit(): void {
    
  }
  checkAdminAndGetAllStudents() {
    this.loginService.sendAdminStatus(this.userService.getIsAdmin()).subscribe(isAdmin => {
      if (isAdmin) {
        this.getAllStudent();
      } else {
        alert("Access denied. Admin privileges required.");
      }
    });
  }

  getAllStudent()
  { 
    this.http.get(`http://localhost:7000/api/student?isadmin=true`)
      .subscribe((resultData: any) => {
        this.isResultLoaded = true;
        console.log(resultData.data);
        this.StudentArray = resultData.data;
        this.updateEmploymentCounts(this.StudentArray);  // Update counts after fetching
      });
  }
  
  updateEmploymentCounts(students: any[]) {
    this.employedCount = 0;
    this.unemployedCount = 0;
    this.seekingEmploymentCount = 0;

    students.forEach(student => {
      const firstChar = student.respondentStatus[0].toLowerCase(); // Get the first character and convert to lowercase
      switch (firstChar) {
        case 'e':
          this.employedCount++;
          break;
        case 'u':
          this.unemployedCount++;
          break;
        case 's':
          this.seekingEmploymentCount++;
          break;
      }
    });
}

  get filteredStudents() {
    const filtered = this.StudentArray.filter(student => {
      return (this.selectedCourse === 'ALL CATEGORIES' || student.respondentCourse === this.selectedCourse) &&
             student.surveyTitle.toLowerCase().split(' ').some((word: string) => 
             word.startsWith(this.searchSurvey.toLowerCase()));
    });
    this.updateEmploymentCounts(filtered);  // Update counts based on filtered data
    return filtered;
  }

  register()
  {
   
    let bodyData = {
      "respondentName" : this.respondentName,
      "respondentEmail" : this.respondentEmail,
      "respondentOccupation" : this.respondentOccupation,
      "respondentCourse" : this.respondentCourse,
      "respondentStatus" : this.respondentStatus
    };
    this.http.post("http://localhost:7000/api/student/add",bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);
        alert("Employee Registered Successfully")
        this.getAllStudent();
    
    });
  }
  setUpdate(data: any) 
  {
   this.respondentName = data.respondentName;
   this.respondentEmail = data.respondentEmail
   this.respondentCourse = data.respondentCourse
   this.respondentOccupation = data.respondentOccupation
   this.respondentStatus = data.respondentStatus

   this.currentStudentID = data.id;
 
  }
  UpdateRecords() {
    let bodyData = {
      "respondentName": this.respondentName,
      "respondentEmail": this.respondentEmail,
      "respondentOccupation" : this.respondentOccupation,
      "respondentCourse" : this.respondentCourse,
      "respondentStatus" : this.respondentStatus
    };
    
    this.http.put("http://localhost:7000/api/student/update/" + this.currentStudentID, bodyData)
      .subscribe((resultData: any) => {
        console.log(resultData);
        alert("Student Updated");
        this.getAllStudent(); // Refresh student list
      }, error => {
        console.error('Error updating student:', error);
        alert("Error updating student. Please try again.");
      });
  }
  
 
  save()
  {
    if(this.currentStudentID == '')
    {
        this.register();
    }
      else
      {
       this.UpdateRecords();
      }       
  }
  setDelete(data: any) {
    if (confirm("Are you sure you want to delete this student?")) {
      this.http.delete("http://localhost:7000/api/student/delete/" + data.id)
        .subscribe((resultData: any) => {
          console.log(resultData);
          alert("Student Deleted");
          this.getAllStudent(); // Refresh student list
        }, error => {
          console.error('Error deleting student:', error);
          alert("Error deleting student. Please try again.");
        });
    }
  }
  
}
