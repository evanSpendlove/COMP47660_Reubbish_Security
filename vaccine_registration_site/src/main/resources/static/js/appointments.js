let stompClient = null;
let isConnected = false;
let today = new Date();
let currentCalendarMonth = -1;
let currentCalendarYear = -1;
let currentSelectedDate = "";
let currentAvailableTimesSelected = null;

function connect() {
    const socket = new SockJS('/appointment-registration');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        isConnected = true;
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/updates', function(messageOutput) {
            updateAvailableAppointments(JSON.parse(messageOutput.body));
        });
    });
	initCalendar();
}


function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function dayClick(clickedDay) {
	let month = String(currentCalendarMonth).padStart(2,'0');
	let year = currentCalendarYear;
	let day = "";
	let clickedChildren = clickedDay.children;
	console.log(clickedChildren);
	if (clickedChildren.length > 0){
		day = clickedChildren[0].innerHTML;
	} else {
		day = clickedDay.innerHTML;
	}
	
	if(clickedDay.classList.contains("prevMonth")){
		if(month == 1){
			month = 12;
			year -= 1;
		}else{
			month -= 1;
		}
	}
	checkAvailability(day+"/"+month+"/"+year);
}

function updateAvailableAppointments(response) {
	currentAvailableTimesSelected = response;
	currentSelectedDate = response["date"];
	var availableTimesDiv = document.getElementById("availableTimesDiv");
	availableTimesDiv.innerHTML = '';
	var h2Title = document.createElement("h2");
	h2Title.innerHTML = "Available Times: " + currentSelectedDate;
	availableTimesDiv.appendChild(h2Title);
	for(let i = 0; i < response["availableTimes"].length; i++){
		let but = document.createElement("button");
		but.innerHTML = response["availableTimes"][i]["time"];
		but.classList.add('btn', 'btn-success', 'mx-1', 'my-1')
		but.addEventListener("click", function(){
			confirmationModal(this);
		})
		availableTimesDiv.appendChild(but);
	}
	var hidden1 = document.getElementById("hidden1");
	hidden1.style.display = "inline";
}

function confirmationModal(clickedAppointment){
	var confirmationArea = document.getElementById("confirmationArea");
	var date = currentSelectedDate;
	var time = clickedAppointment.innerHTML;
	var dateField = document.getElementById("date");
	dateField.value = date;
	var timeField = document.getElementById("time");
	timeField.value = time;
	var hidden1 = document.getElementById("hidden1");
	hidden1.style.display = "none";
	confirmationArea.style.display="inline";
}

function closeConfirmation(){
	var confirmationArea = document.getElementById("confirmationArea");
	confirmationArea.style.display="none";
	updateAvailableAppointments(currentAvailableTimesSelected);
}

function checkAvailability(date) {
    stompClient.send("/app/appointment-registration", {}, JSON.stringify({"date": date}));
}

function initCalendar(){
	updateCalendar(today);
}

function moveCalendarBack(){
	let newMonth = currentCalendarMonth -1;
	let newYear = currentCalendarYear;
	if (newMonth == 0){
		newMonth = 12;
		newYear -= 1;
	}
	if (!(newMonth == today.getMonth() && newYear == today.getFullYear())){
		updateCalendar(new Date(String(newMonth).padStart(2,'0')+'/01/'+newYear));
	}
}

function moveCalendarForward(){
	let newMonth = currentCalendarMonth + 1;
	let newYear = currentCalendarYear;
	if (newMonth == 13){
		newMonth = 1;
		newYear += 1;
	}
	if(!(newMonth == (today.getMonth()+2) && newYear == (today.getFullYear()+1))){
		updateCalendar(new Date(String(newMonth).padStart(2,'0')+'/01/'+newYear));
	}
}

function updateCalendar(date){
	let day = String(date.getDate());
	let month = String(date.getMonth() + 1).padStart(2,'0');
	let year = date.getFullYear();
	currentCalendarMonth = date.getMonth() + 1;
	currentCalendarYear = year;

	let startDay = getDayOf1stMonth(month+'/'+"01"+'/'+year);
	let lastMonthEndNum = getLastNumOfLastMonth(date.getMonth()-1);
	let lastNumOfCurrentMonth = getLastNumOfLastMonth(date.getMonth());
	let [startNum, numOfExtraDays ] = getStartNum(startDay, lastMonthEndNum);
	
	let ul = document.getElementById("days");
	ul.innerHTML = '';
	var monthTextSpan = document.getElementById("monthText");
	monthTextSpan.innerHTML = '';
	monthTextSpan.appendChild(document.createTextNode(convertStringMonthToInteger(month)));
	var yearTextSpan = document.getElementById("yearText");
	yearTextSpan.innerHTML = '';
	yearTextSpan.appendChild(document.createTextNode(year));
	for (let i = 0; i < numOfExtraDays; i++){
		var li = document.createElement("li");
		li.appendChild(document.createTextNode(String(startNum+i)));
		li.classList.add("prevMonth");
		li.addEventListener("click", function(){
			dayClick(this);
		});
		ul.appendChild(li);
	}
	for (let i = 0; i < lastNumOfCurrentMonth; i++){
		var li = document.createElement("li");
		if (i+1 == today.getDate() && month == String(today.getMonth() + 1).padStart(2,'0') && year == today.getFullYear()){
			var span = document.createElement("span");
			span.appendChild(document.createTextNode(String(i+1)))
			span.classList.add("active");
			li.append(span);
		}else{
			li.appendChild(document.createTextNode(String(i+1)));
		}
		if (!((i+1 < today.getDate() && month == (today.getMonth() + 1)) || month < (today.getMonth() + 1))){
			li.addEventListener("click", function(){
				dayClick(this);
			});
		}
		ul.appendChild(li);
	}
}

function getDayOf1stMonth(date) {
	const d = new Date(date);
	return d.getDay();
}

function getStartNum(startDay, lastMonthEndNum){
	if (startDay == 1){
		return [1,0];
	}
	if (startDay == 0){
		startDay += 7;
	}
	var offset = startDay - 2;
	return [(lastMonthEndNum - offset), (offset+1)];	
}

function getLastNumOfLastMonth(month) {
	if (month == -1){
		month = 11;
	}
	if ((month <=6 && (month % 2) == 0) || (month >= 7 && (month % 2) != 0)){
		return 31;
	} else {
		if (month == 1){
			return 28;
		}
		else{
			return 30;
		}
	}
}


function convertStringMonthToInteger(month){
	var output = "";
	switch(month) {
		case "01":
			output = "January";
			break;
		case "02":
			output = "February";
			break;
		case "03":
			output = "March";
			break;
		case "04":
			output = "April";
			break;
		case "05":
			output = "May";
			break;
		case "06":
			output = "June";
			break;
		case "07":
			output = "July";
			break;
		case "08":
			output = "August";
			break;
		case "09":
			output = "September";
			break;
		case "10":
			output = "October";
			break;
		case "11":
			output = "November";
			break;
		case "12":
			output = "December";
			break;
	  }
	return output;
}

