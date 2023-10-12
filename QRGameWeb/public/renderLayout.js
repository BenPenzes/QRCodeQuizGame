function renderLayout_MaximizeMap() {
    document.getElementById("td_map").style.width = "100%";
    document.getElementById("div_teamsList").style.width = "0%";
    document.getElementById("div_teamsList_content").style.display = "none";
}
function renderLayout_showTeams() {
    document.getElementById("td_map").style.width = "70%";    
    document.getElementById("div_teamsList").style.width = "30%";
    document.getElementById("div_teamsList_content").style.display = "inline";
}
