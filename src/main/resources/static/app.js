const API = "/api";

let allSessions = [];
let currentSessionFilter = "ALL";


/* =====================================================
   API
===================================================== */

async function api(url, options = {}) {

    const response = await fetch(API + url, {
        headers: {
            "Content-Type": "application/json"
        },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();

    let data = {};

    try {
        data = text ? JSON.parse(text) : {};
    } catch {
        data = {};
    }

    if (!response.ok) {
        throw new Error(
            data.message ||
            data.error ||
            "Something went wrong"
        );
    }

    return data;
}


/* =====================================================
   PAGE NAVIGATION
===================================================== */

function showPage(page) {

    document
        .querySelectorAll(".page")
        .forEach(p =>
            p.classList.remove("active-page")
        );

    const selectedPage =
        document.getElementById(page);

    if (selectedPage) {
        selectedPage.classList.add("active-page");
    }

    document
        .querySelectorAll(".nav-btn")
        .forEach(btn =>
            btn.classList.remove("active")
        );

    const buttons =
        document.querySelectorAll(".nav-btn");

    buttons.forEach(btn => {

        const buttonText =
            btn.innerText.toLowerCase();

        const targetText =
            page === "matching"
                ? "smart matching"
                : page;

        if (buttonText.includes(targetText)) {
            btn.classList.add("active");
        }

    });

    const titles = {
        dashboard: "Dashboard",
        alumni: "Alumni",
        students: "Students",
        matching: "Smart Matching",
        sessions: "Sessions",
        reports: "Reports"
    };

    const pageTitle =
        document.getElementById("pageTitle");

    if (pageTitle) {
        pageTitle.innerText =
            titles[page] || "MentorConnect";
    }


    if (page === "dashboard") {
        loadDashboard();
    }

    if (page === "alumni") {
        loadAlumni();
    }

    if (page === "students") {
        loadStudents();
    }

    if (page === "matching") {
        loadMatchingStudents();
    }

    if (page === "sessions") {
        loadSessions();
    }

    if (page === "reports") {
        loadReports();
    }
}


/* =====================================================
   DASHBOARD
===================================================== */

async function loadDashboard() {

    try {

        const data =
            await api("/reports/dashboard");

        setText(
            "alumniCount",
            data.alumniCount ?? 0
        );

        setText(
            "studentCount",
            data.studentCount ?? 0
        );

        setText(
            "pairCount",
            data.activePairCount ?? 0
        );

        setText(
            "sessionCount",
            data.totalSessionCount ?? 0
        );

        setText(
            "completedCount",
            data.completedSessionCount ?? 0
        );

        await loadSessionCounts();

    } catch (error) {

        alert(error.message);

    }
}


function setText(id, value) {

    const element =
        document.getElementById(id);

    if (element) {
        element.innerText = value;
    }
}


/* =====================================================
   SESSION COUNTS
===================================================== */

async function loadSessionCounts() {

    try {

        const sessions =
            await api("/sessions");

        const total =
            sessions.length;

        const scheduled =
            sessions.filter(
                s =>
                    (s.status || "SCHEDULED")
                        .toUpperCase()
                    === "SCHEDULED"
            ).length;

        const completed =
            sessions.filter(
                s =>
                    (s.status || "")
                        .toUpperCase()
                    === "COMPLETED"
            ).length;

        const cancelled =
            sessions.filter(
                s =>
                    (s.status || "")
                        .toUpperCase()
                    === "CANCELLED"
            ).length;

        setText("sessionCount", total);
        setText("scheduledCount", scheduled);
        setText("completedCount", completed);
        setText("cancelledCount", cancelled);

    } catch (error) {

        console.error(
            "Could not load session counts:",
            error
        );

    }
}


/* =====================================================
   ALUMNI
===================================================== */

async function loadAlumni() {

    try {

        const alumni =
            await api("/alumni");

        const container =
            document.getElementById("alumniList");

        if (!container) {
            return;
        }

        container.innerHTML = "";

        if (!alumni.length) {

            container.innerHTML = `
                <div class="card">
                    <h3>No Alumni Found</h3>
                    <p>Add an alumni mentor to get started.</p>
                </div>
            `;

            return;
        }

        alumni.forEach(a => {

            const tags =
                (a.interestTags || [])
                    .map(
                        t =>
                            `<span class="tag">
                                ${escapeHtml(
                                t.name || t
                            )}
                            </span>`
                    )
                    .join("");

            container.innerHTML += `

                <div class="card">

                    <h3>
                        ${escapeHtml(a.name)}
                    </h3>

                    <p>
                        ${escapeHtml(
                a.designation || ""
            )}

                        ${
                a.company
                    ? " • " +
                    escapeHtml(a.company)
                    : ""
            }
                    </p>

                    <p>
                        ${escapeHtml(a.email || "")}
                    </p>

                    <p>
                        Capacity:
                        ${a.maxConcurrentMentees ?? 0}
                    </p>

                    <p>
                        Available:
                        ${
                escapeHtml(
                    a.availableSlots || "Not specified"
                )
            }
                    </p>

                    <div class="tags">
                        ${tags}
                    </div>

                </div>
            `;
        });

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   ALUMNI FORM
===================================================== */

function openAlumniForm() {

    document.getElementById("modalBody").innerHTML = `

        <h2>Add Alumni</h2>

        <br>

        <div class="form">

            <label>Name</label>

            <input
                id="aName"
                placeholder="Full name">


            <label>Email</label>

            <input
                id="aEmail"
                type="email"
                placeholder="Email">


            <label>Phone</label>

            <input
                id="aPhone"
                placeholder="Phone">


            <label>Company</label>

            <input
                id="aCompany"
                placeholder="Company">


            <label>Designation</label>

            <input
                id="aDesignation"
                placeholder="Designation">


            <label>
                Maximum Concurrent Mentees
            </label>

            <input
                id="aCapacity"
                type="number"
                min="1"
                value="2">


            <label>Available Slots</label>

            <textarea
                id="aSlots"
                placeholder="Saturday 10 AM">
            </textarea>


            <label>Interest Tags</label>

            <input
                id="aTags"
                placeholder="Java, Spring Boot, MySQL">


            <button onclick="createAlumni()">
                Save Alumni
            </button>

        </div>
    `;

    openModal();
}


/* =====================================================
   CREATE ALUMNI
===================================================== */

async function createAlumni() {

    const data = {

        name:
        document.getElementById("aName").value,

        email:
        document.getElementById("aEmail").value,

        phone:
        document.getElementById("aPhone").value,

        company:
        document.getElementById("aCompany").value,

        designation:
        document.getElementById("aDesignation").value,

        maxConcurrentMentees:
            Number(
                document.getElementById("aCapacity").value
            ),

        availableSlots:
        document.getElementById("aSlots").value,

        active: true,

        interestTags:
            document
                .getElementById("aTags")
                .value
                .split(",")
                .map(x => x.trim())
                .filter(Boolean)
    };


    try {

        await api("/alumni", {
            method: "POST",
            body: JSON.stringify(data)
        });

        closeModal();

        alert("Alumni added successfully!");

        await loadAlumni();
        await loadDashboard();

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   STUDENTS
===================================================== */

async function loadStudents() {

    try {

        const students =
            await api("/students");

        const container =
            document.getElementById("studentList");

        if (!container) {
            return;
        }

        container.innerHTML = "";

        if (!students.length) {

            container.innerHTML = `
                <div class="card">
                    <h3>No Students Found</h3>
                    <p>Add a student to get started.</p>
                </div>
            `;

            return;
        }

        students.forEach(s => {

            const tags =
                (s.interestTags || [])
                    .map(
                        t =>
                            `<span class="tag">
                                ${escapeHtml(
                                t.name || t
                            )}
                            </span>`
                    )
                    .join("");

            container.innerHTML += `

                <div class="card">

                    <h3>
                        ${escapeHtml(s.name)}
                    </h3>

                    <p>
                        ${escapeHtml(
                s.department || ""
            )}

                        • Year
                        ${s.yearOfStudy ?? ""}
                    </p>

                    <p>
                        ${escapeHtml(s.email || "")}
                    </p>

                    <div class="tags">
                        ${tags}
                    </div>

                </div>
            `;
        });

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   STUDENT FORM
===================================================== */

function openStudentForm() {

    document.getElementById("modalBody").innerHTML = `

        <h2>Add Student</h2>

        <br>

        <div class="form">

            <label>Name</label>

            <input
                id="sName"
                placeholder="Full name">


            <label>Email</label>

            <input
                id="sEmail"
                type="email"
                placeholder="Email">


            <label>Department</label>

            <input
                id="sDepartment"
                placeholder="ECE">


            <label>Year of Study</label>

            <input
                id="sYear"
                type="number"
                min="1"
                max="6"
                value="2">


            <label>Interest Tags</label>

            <input
                id="sTags"
                placeholder="Java, Spring Boot">


            <button onclick="createStudent()">
                Save Student
            </button>

        </div>
    `;

    openModal();
}


/* =====================================================
   CREATE STUDENT
===================================================== */

async function createStudent() {

    const data = {

        name:
        document.getElementById("sName").value,

        email:
        document.getElementById("sEmail").value,

        department:
        document.getElementById("sDepartment").value,

        yearOfStudy:
            Number(
                document.getElementById("sYear").value
            ),

        interestTags:
            document
                .getElementById("sTags")
                .value
                .split(",")
                .map(x => x.trim())
                .filter(Boolean)
    };


    try {

        await api("/students", {
            method: "POST",
            body: JSON.stringify(data)
        });

        closeModal();

        alert("Student added successfully!");

        await loadStudents();
        await loadMatchingStudents();
        await loadDashboard();

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   MATCHING
===================================================== */

async function loadMatchingStudents() {

    try {

        const students =
            await api("/students");

        const select =
            document.getElementById(
                "matchingStudent"
            );

        if (!select) {
            return;
        }

        select.innerHTML = `
            <option value="">
                Select a student
            </option>
        `;

        students.forEach(student => {

            select.innerHTML += `

                <option value="${student.id}">

                    ${escapeHtml(student.name)}

                </option>
            `;
        });

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   FIND MATCHES
===================================================== */

async function findMatches() {

    const studentId =
        document.getElementById(
            "matchingStudent"
        ).value;

    if (!studentId) {

        alert(
            "Please select a student"
        );

        return;
    }


    try {

        const matches =
            await api(
                `/matches/suggestions/student/${studentId}`
            );

        const container =
            document.getElementById(
                "matchList"
            );

        container.innerHTML = "";


        if (!matches.length) {

            container.innerHTML = `

                <div class="card">

                    <h3>
                        No matching mentors
                    </h3>

                    <p>
                        No active mentor currently
                        matches this student's interests.
                    </p>

                </div>
            `;

            return;
        }


        matches.forEach(match => {

            const tags =
                (match.overlappingTags || [])
                    .map(
                        tag =>
                            `<span class="tag">
                                ${escapeHtml(tag)}
                            </span>`
                    )
                    .join("");

            container.innerHTML += `

                <div class="card">

                    <div class="score">
                        ${match.overlapCount ?? 0}
                    </div>

                    <h3>
                        ${escapeHtml(
                match.alumniName || ""
            )}
                    </h3>

                    <p>
                        ${escapeHtml(
                match.designation || ""
            )}

                        ${
                match.company
                    ? " • " +
                    escapeHtml(match.company)
                    : ""
            }
                    </p>

                    <p>
                        Capacity:
                        ${match.currentMentees ?? 0}
                        /
                        ${match.maxConcurrentMentees ?? 0}
                    </p>

                    <p>
                        ${escapeHtml(
                match.availableSlots || ""
            )}
                    </p>

                    <div class="tags">
                        ${tags}
                    </div>

                    <br>

                    <button
                        onclick="
                            createMatch(
                                ${match.alumniId},
                                ${studentId}
                            )
                        ">

                        Request Match

                    </button>

                </div>
            `;
        });

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   CREATE MATCH
===================================================== */

async function createMatch(
    alumniId,
    studentId
) {

    try {

        await api("/matches", {

            method: "POST",

            body: JSON.stringify({

                alumniId:
                    Number(alumniId),

                studentId:
                    Number(studentId)

            })

        });

        alert(
            "Mentorship pair created successfully!"
        );

        await loadDashboard();

        await findMatches();

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   LOAD SESSIONS
===================================================== */

async function loadSessions() {

    try {

        allSessions =
            await api("/sessions");

        displaySessions(
            getFilteredSessions()
        );

        updateSessionCounts();

    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   FILTERED SESSIONS
===================================================== */

function getFilteredSessions() {

    if (currentSessionFilter === "ALL") {
        return allSessions;
    }

    return allSessions.filter(
        session =>
            (
                session.status ||
                "SCHEDULED"
            ).toUpperCase()
            === currentSessionFilter
    );
}


/* =====================================================
   DISPLAY SESSIONS
===================================================== */

function displaySessions(sessions) {

    const container =
        document.getElementById(
            "sessionList"
        );

    if (!container) {
        return;
    }

    container.innerHTML = "";


    if (!sessions || sessions.length === 0) {

        container.innerHTML = `

            <div class="card">

                <h3>
                    No Sessions Found
                </h3>

                <p>
                    No sessions are available
                    for this category.
                </p>

            </div>
        `;

        return;
    }


    sessions.forEach(s => {

        const status =
            (
                s.status ||
                "SCHEDULED"
            ).toUpperCase();


        let statusClass =
            "status-scheduled";


        if (status === "COMPLETED") {
            statusClass =
                "status-completed";
        }


        if (status === "CANCELLED") {
            statusClass =
                "status-cancelled";
        }


        let actionButtons = "";


        /*
         * SCHEDULED
         */

        if (status === "SCHEDULED") {

            actionButtons = `

                <button
                    onclick="
                        completeSession(
                            ${s.id}
                        )
                    ">

                    Mark Completed

                </button>


                <button
                    class="cancel-session-btn"
                    onclick="
                        cancelSession(
                            ${s.id}
                        )
                    ">

                    Cancel Session

                </button>
            `;
        }


        /*
         * COMPLETED
         */

        else if (status === "COMPLETED") {

            actionButtons = `

                <span class="session-message">

                    ✓ Session Completed

                </span>
            `;
        }


        /*
         * CANCELLED
         */

        else if (status === "CANCELLED") {

            actionButtons = `

                <span class="session-message">

                    ✕ Session Cancelled

                </span>
            `;
        }


        const pairId =
            s.pair
                ? s.pair.id
                : "N/A";


        const scheduledDate =
            s.scheduledAt
                ? new Date(
                    s.scheduledAt
                ).toLocaleString()
                : "Not specified";


        container.innerHTML += `

            <div class="card session-card">

                <div class="session-header">

                    <h3>
                        ${
            escapeHtml(
                s.topic ||
                "Mentorship Session"
            )
        }
                    </h3>

                    <span
                        class="
                            session-status
                            ${statusClass}
                        ">

                        ${status}

                    </span>

                </div>


                <p>

                    <strong>
                        Pair ID:
                    </strong>

                    ${pairId}

                </p>


                <p>

                    <strong>
                        Scheduled:
                    </strong>

                    ${scheduledDate}

                </p>


                <p>

                    <strong>
                        Notes:
                    </strong>

                    ${
            escapeHtml(
                s.notes ||
                "No notes"
            )
        }

                </p>


                ${
            status === "CANCELLED"

                ? `

                        <p>

                            <strong>
                                Cancelled By:
                            </strong>

                            ${
                    escapeHtml(
                        s.cancelledBy ||
                        "MENTOR"
                    )
                }

                        </p>


                        <p>

                            <strong>
                                Cancellation Reason:
                            </strong>

                            ${
                    escapeHtml(
                        s.cancellationReason ||
                        "No reason provided"
                    )
                }

                        </p>

                    `

                : ""
        }


                <div class="session-actions">

                    ${actionButtons}

                </div>

            </div>
        `;
    });
}


/* =====================================================
   FILTER SESSIONS
===================================================== */

function filterSessions(
    status,
    button
) {

    currentSessionFilter =
        status;


    document
        .querySelectorAll(
            ".session-filter"
        )
        .forEach(btn =>
            btn.classList.remove("active")
        );


    if (button) {

        button.classList.add("active");

    }


    displaySessions(
        getFilteredSessions()
    );
}


/* =====================================================
   MENTOR CANCEL SESSION
===================================================== */

function cancelSession(id) {

    document.getElementById(
        "modalBody"
    ).innerHTML = `

        <h2>
            Cancel Mentoring Session
        </h2>

        <br>

        <p class="cancel-info">
            Please provide a reason for cancelling
            this session.
        </p>

        <br>

        <div class="form">

            <label>
                Cancellation Reason
            </label>

            <textarea
                id="cancellationReason"
                rows="5"
                placeholder="Example: Unable to attend due to schedule conflict">
            </textarea>


            <button
                class="cancel-confirm-btn"
                onclick="
                    confirmCancelSession(
                        ${id}
                    )
                ">

                Confirm Cancellation

            </button>


            <button
                class="keep-session-btn"
                onclick="closeModal()">

                Keep Session

            </button>

        </div>
    `;

    openModal();
}


/* =====================================================
   CONFIRM MENTOR CANCELLATION
===================================================== */

async function confirmCancelSession(id) {

    const reason =
        document.getElementById(
            "cancellationReason"
        ).value.trim();


    if (!reason) {

        alert(
            "Please enter a cancellation reason."
        );

        return;
    }


    try {

        await api(
            `/sessions/${id}/cancel`,
            {

                method: "PUT",

                body: JSON.stringify({

                    cancelledBy: "MENTOR",

                    cancellationReason:
                    reason

                })

            }
        );


        closeModal();


        alert(
            "Session cancelled successfully!"
        );


        await loadSessions();

        await loadDashboard();


    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   COMPLETE SESSION
===================================================== */

async function completeSession(id) {

    try {

        await api(
            `/sessions/${id}/status`,
            {

                method: "PUT",

                body: JSON.stringify({

                    status: "COMPLETED"

                })

            }
        );


        alert(
            "Session marked as completed!"
        );


        await loadSessions();

        await loadDashboard();


    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   UPDATE SESSION COUNTS
===================================================== */

function updateSessionCounts() {

    const total =
        allSessions.length;


    const scheduled =
        allSessions.filter(
            s =>
                (
                    s.status ||
                    "SCHEDULED"
                ).toUpperCase()
                === "SCHEDULED"
        ).length;


    const completed =
        allSessions.filter(
            s =>
                (
                    s.status ||
                    ""
                ).toUpperCase()
                === "COMPLETED"
        ).length;


    const cancelled =
        allSessions.filter(
            s =>
                (
                    s.status ||
                    ""
                ).toUpperCase()
                === "CANCELLED"
        ).length;


    setText(
        "sessionCount",
        total
    );

    setText(
        "scheduledCount",
        scheduled
    );

    setText(
        "completedCount",
        completed
    );

    setText(
        "cancelledCount",
        cancelled
    );
}


/* =====================================================
   SESSION FORM
===================================================== */

function openSessionForm() {

    document.getElementById(
        "modalBody"
    ).innerHTML = `

        <h2>
            Schedule Session
        </h2>

        <br>

        <div class="form">

            <label>
                Mentorship Pair ID
            </label>

            <input
                id="pairId"
                type="number"
                placeholder="Example: 1">


            <label>
                Date and Time
            </label>

            <input
                id="sessionDate"
                type="datetime-local">


            <label>
                Topic
            </label>

            <input
                id="sessionTopic"
                placeholder="Spring Boot Project">


            <label>
                Notes
            </label>

            <textarea
                id="sessionNotes"
                placeholder="Enter session notes">
            </textarea>


            <button
                onclick="createSession()">

                Schedule Session

            </button>

        </div>
    `;

    openModal();
}


/* =====================================================
   CREATE SESSION
===================================================== */

async function createSession() {

    const pairId =
        Number(
            document.getElementById(
                "pairId"
            ).value
        );


    const scheduledAt =
        document.getElementById(
            "sessionDate"
        ).value;


    const topic =
        document.getElementById(
            "sessionTopic"
        ).value;


    const notes =
        document.getElementById(
            "sessionNotes"
        ).value;


    if (!pairId) {

        alert(
            "Please enter the mentorship pair ID."
        );

        return;
    }


    if (!scheduledAt) {

        alert(
            "Please select the session date and time."
        );

        return;
    }


    const data = {

        pairId: pairId,

        scheduledAt: scheduledAt,

        topic: topic,

        notes: notes,

        status: "SCHEDULED"
    };


    try {

        await api("/sessions", {

            method: "POST",

            body: JSON.stringify(data)

        });


        closeModal();


        alert(
            "Session scheduled successfully!"
        );


        currentSessionFilter =
            "ALL";


        document
            .querySelectorAll(
                ".session-filter"
            )
            .forEach(
                (button, index) => {

                    button.classList.toggle(
                        "active",
                        index === 0
                    );

                }
            );


        await loadSessions();

        await loadDashboard();


    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   REPORTS
===================================================== */

async function loadReports() {

    try {

        const reports =
            await api(
                "/reports/engagement"
            );


        const container =
            document.getElementById(
                "reportList"
            );


        if (!container) {
            return;
        }


        if (!reports.length) {

            container.innerHTML = `

                <div class="card">

                    <h3>
                        No Report Data
                    </h3>

                    <p>
                        No mentorship engagement
                        data is available.
                    </p>

                </div>
            `;

            return;
        }


        let html = `

            <table class="report-table">

                <thead>

                    <tr>

                        <th>Pair</th>

                        <th>Alumni</th>

                        <th>Student</th>

                        <th>Match Score</th>

                        <th>Total</th>

                        <th>Completed</th>

                        <th>Scheduled</th>

                        <th>Cancelled</th>

                    </tr>

                </thead>

                <tbody>
        `;


        reports.forEach(r => {

            html += `

                <tr>

                    <td>
                        #${r.pairId}
                    </td>

                    <td>
                        ${escapeHtml(
                r.alumniName || ""
            )}
                    </td>

                    <td>
                        ${escapeHtml(
                r.studentName || ""
            )}
                    </td>

                    <td>
                        ${r.matchScore ?? 0}
                    </td>

                    <td>
                        ${r.totalSessions ?? 0}
                    </td>

                    <td>
                        ${r.completedSessions ?? 0}
                    </td>

                    <td>
                        ${r.scheduledSessions ?? 0}
                    </td>

                    <td>
                        ${r.cancelledSessions ?? 0}
                    </td>

                </tr>
            `;
        });


        html += `

                </tbody>

            </table>
        `;


        container.innerHTML =
            html;


    } catch (error) {

        alert(error.message);

    }
}


/* =====================================================
   MODAL
===================================================== */

function openModal() {

    document
        .getElementById("modal")
        .classList.add("show");
}


function closeModal() {

    document
        .getElementById("modal")
        .classList.remove("show");
}


/* =====================================================
   ESCAPE HTML
===================================================== */

function escapeHtml(value) {

    if (value === null ||
        value === undefined) {

        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


/* =====================================================
   INITIAL LOAD
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    () => {

        loadDashboard();

    }
);